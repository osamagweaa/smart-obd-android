package com.example.obdapp.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Singleton
class BluetoothObdService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    val isConnected: Boolean
        get() = socket?.isConnected == true && input != null && output != null

    @SuppressLint("MissingPermission")
    fun pairedElmDevices(): List<BluetoothDevice> {
        val keywords = listOf("OBD", "ELM", "OBDII", "TORQUE", "VGATE", "KONNWEI")
        return adapter?.bondedDevices
            ?.filter { device ->
                val name = device.name.orEmpty().uppercase()
                keywords.any { it in name }
            }
            ?.sortedBy { it.name ?: it.address }
            .orEmpty()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connectByName(deviceNameOrAddress: String) = withContext(Dispatchers.IO) {
        require(deviceNameOrAddress.isNotBlank()) { "Nombre de adaptador vacío" }
        val device = adapter?.bondedDevices?.firstOrNull {
            it.name.equals(deviceNameOrAddress, ignoreCase = true) || it.address == deviceNameOrAddress
        } ?: throw IOException("Adaptador no encontrado")
        connect(device)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connect(device: BluetoothDevice) {
        close()
        socket = try {
            device.createRfcommSocketToServiceRecord(uuid).also { it.connect() }
        } catch (secureError: IOException) {
            try {
                device.createInsecureRfcommSocketToServiceRecord(uuid).also { it.connect() }
            } catch (insecureError: IOException) {
                throw IOException("No se pudo conectar con ${device.name ?: "el adaptador"}", insecureError)
            }
        }
        input = socket?.inputStream
        output = socket?.outputStream
    }

    suspend fun initializeElm() {
        listOf("AT D", "AT Z", "AT E0", "AT L0", "AT S0", "AT H0", "AT SP 0", "AT ST FF")
            .forEach { command ->
                runCatching { sendCommand(command, delayMs = 250) }
            }
        runCatching { sendCommand("0100", delayMs = 300) }
    }

    suspend fun sendCommand(command: String, delayMs: Long = 180L): String = withContext(Dispatchers.IO) {
        val out = output ?: throw IOException("Adaptador no conectado")
        val inStream = input ?: throw IOException("Adaptador no conectado")

        out.write((command.replace(" ", "") + "\r").toByteArray(Charsets.US_ASCII))
        out.flush()
        delay(delayMs)

        val buffer = StringBuilder()
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 4_000L) {
            while (inStream.available() > 0) {
                val char = inStream.read().toChar()
                if (char == '>') {
                    return@withContext buffer.toString().sanitizeElmResponse()
                }
                buffer.append(char)
            }
            delay(20)
        }
        buffer.toString().sanitizeElmResponse()
    }

    fun close() {
        runCatching { socket?.close() }
        socket = null
        input = null
        output = null
    }

    private fun String.sanitizeElmResponse(): String =
        replace("\r", " ")
            .replace("\n", " ")
            .replace("SEARCHING...", "", ignoreCase = true)
            .replace(">", "")
            .trim()
}
