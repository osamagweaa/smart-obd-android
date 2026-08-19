package com.squillaci.autodiag.bluetoothmanager

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.annotation.RequiresPermission
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ObdJavaApiDataSource : ObdDataSource {
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null
    private var connected = false

    @SuppressLint("MissingPermission")
    override suspend fun connect(mac: String) {
        val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac)
        connectSocket(device.name ?: mac, device.createRfcommSocketToServiceRecord(uuid)) {
            device.createInsecureRfcommSocketToServiceRecord(uuid)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connectByName(mac: String) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val device = adapter.bondedDevices.firstOrNull { it.name == mac || it.address == mac }
            ?: throw IOException("Device not found")
        connectSocket(device.name ?: mac, device.createRfcommSocketToServiceRecord(uuid)) {
            device.createInsecureRfcommSocketToServiceRecord(uuid)
        }
    }

    private suspend fun connectSocket(
        label: String,
        secureSocket: BluetoothSocket,
        insecureFactory: () -> BluetoothSocket
    ) = withContext(Dispatchers.IO) {
        disconnect()
        socket = try {
            secureSocket.also { it.connect() }
        } catch (secureError: IOException) {
            Log.w("OBD", "Secure socket failed for $label, trying insecure", secureError)
            insecureFactory().also { it.connect() }
        }
        input = socket?.inputStream
        output = socket?.outputStream
        connected = true
        initElm()
    }

    private suspend fun initElm() {
        listOf("AT D", "AT Z", "AT E0", "AT L0", "AT S0", "AT H0", "AT SP 0", "AT ST FF")
            .forEach { runCatching { sendRaw(it) } }
        runCatching { sendRaw("0100") }
    }

    override suspend fun readSpeed(): String = "${readSpeedNoUnit()} km/h"
    suspend fun readSpeedNoUnit(): String = readPidValue("010D", "0D")?.toInt()?.toString() ?: "--"
    suspend fun readRPMNoUnit(): String = readPidValue("010C", "0C")?.toInt()?.toString() ?: "--"
    suspend fun readTemEngineNoUnit(): String = readEngineTempNoUnit()
    override suspend fun readEngineTemp(): String = "${readEngineTempNoUnit()} °C"
    suspend fun readEngineTempNoUnit(): String = readPidValue("0105", "05")?.toInt()?.toString() ?: "--"
    suspend fun readAirIntakeTemperatureCommand(): String = "${readAirIntakeTemperatureNoUnitCommand()} °C"
    suspend fun readAirIntakeTemperatureNoUnitCommand(): String = readPidValue("010F", "0F")?.toInt()?.toString() ?: "--"
    suspend fun readThrottlePositionCommand(): String = "${readThrottlePositionNoUnitCommand()} %"
    suspend fun readThrottlePositionNoUnitCommand(): String = readPidValue("0111", "11")?.toInt()?.toString() ?: "--"
    override suspend fun readRpm(): String = "${readRPMNoUnit()} RPM"
    suspend fun readModuleVoltageCommand(): String = readModuleVoltageNoUnit()
    suspend fun readModuleVoltageNoUnit(): String = sendRaw("ATRV").ifBlank { "--" }
    suspend fun readVINCommand(): String = sendRaw("0902")

    override suspend fun readDtc(): String {
        val response = sendRaw("03", delayMs = 350)
        return Regex("[PCBU][0-3][0-9A-F]{3}", RegexOption.IGNORE_CASE)
            .findAll(response)
            .joinToString("\n") { it.value.uppercase() }
    }

    private suspend fun readPidValue(command: String, pid: String): Float? {
        val response = sendRaw(command)
        if (response.contains("NO DATA", ignoreCase = true)) return null
        val bytes = Regex("[0-9A-Fa-f]{2}").findAll(response).map { it.value.toInt(16) }.toList()
        val index = bytes.windowed(2).indexOfFirst { it[0] == 0x41 && it[1] == pid.toInt(16) }
        if (index < 0) return null
        val a = bytes.getOrNull(index + 2) ?: return null
        val b = bytes.getOrNull(index + 3) ?: 0
        return when (pid) {
            "0C" -> ((a * 256) + b) / 4f
            "0D" -> a.toFloat()
            "05", "0F" -> (a - 40).toFloat()
            "11" -> a * 100f / 255f
            else -> null
        }
    }

    private suspend fun sendRaw(command: String, delayMs: Long = 180L): String = withContext(Dispatchers.IO) {
        if (!connected) throw IllegalStateException("Not connected to OBD")
        val out = output ?: throw IllegalStateException("Not connected to OBD")
        val inStream = input ?: throw IllegalStateException("Not connected to OBD")
        out.write((command.replace(" ", "") + "\r").toByteArray(Charsets.US_ASCII))
        out.flush()
        delay(delayMs)

        val buffer = StringBuilder()
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 4_000L) {
            while (inStream.available() > 0) {
                val char = inStream.read().toChar()
                if (char == '>') return@withContext buffer.toString().cleanElm()
                buffer.append(char)
            }
            delay(20)
        }
        buffer.toString().cleanElm()
    }

    override fun disconnect() {
        runCatching { socket?.close() }
        socket = null
        input = null
        output = null
        connected = false
    }

    fun disconnectOBD() = disconnect()

    private fun String.cleanElm(): String =
        replace("\r", " ").replace("\n", " ").replace(">", "").trim()
}
