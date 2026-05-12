package com.example.obdapp.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.example.obdapp.data.bluetooth.BluetoothObdService
import com.example.obdapp.data.dtc.DtcDatabase
import com.example.obdapp.data.obd.ObdCommandExecutor
import com.example.obdapp.domain.model.AutoDiagPid
import com.example.obdapp.domain.model.DpfStatus
import com.example.obdapp.domain.model.DtcResult
import com.example.obdapp.domain.model.FuelType
import com.example.obdapp.domain.model.ItvMonitor
import com.example.obdapp.domain.model.LiveData
import com.example.obdapp.domain.model.LivePid
import com.example.obdapp.domain.model.ObdConnectionState
import com.example.obdapp.domain.model.VehicleInfo
import com.example.obdapp.domain.repository.ObdRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class ObdRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothService: BluetoothObdService,
    private val executor: ObdCommandExecutor,
    private val dtcDatabase: DtcDatabase
) : ObdRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val preferences = context.getSharedPreferences("autodiag_obd", Context.MODE_PRIVATE)
    private var pollingJob: Job? = null
    private var dieselFallback: Boolean? = null
    private val oilTempTrend = ArrayDeque<Int>()

    private val _connectionState = MutableStateFlow<ObdConnectionState>(ObdConnectionState.Idle)
    override val connectionState: StateFlow<ObdConnectionState> = _connectionState

    private val _vehicleInfo = MutableStateFlow(VehicleInfo())
    override val vehicleInfo: StateFlow<VehicleInfo> = _vehicleInfo

    private val _liveData = MutableStateFlow(LiveData.Empty)
    override val liveData: StateFlow<LiveData> = _liveData

    private val _dtcResult = MutableStateFlow(DtcResult())
    override val dtcResult: StateFlow<DtcResult> = _dtcResult

    private val _itvMonitors = MutableStateFlow(executor.defaultReadiness())
    override val itvMonitors: StateFlow<List<ItvMonitor>> = _itvMonitors

    override suspend fun autoReconnect() {
        val lastDevice = preferences.getString(KEY_LAST_DEVICE, null) ?: return
        if (connectionState.value is ObdConnectionState.Connected) return
        runCatching { connectToDevice(lastDevice) }
    }

    @SuppressLint("MissingPermission")
    override suspend fun connectToDevice(deviceName: String) {
        _connectionState.value = ObdConnectionState.Connecting
        val result = retryConnection(deviceName)
        if (result.isFailure) {
            _connectionState.value = ObdConnectionState.Error(
                "Adaptador no encontrado. Asegúrate de que el ELM327 está enchufado y emparejado por Bluetooth."
            )
            return
        }

        preferences.edit().putString(KEY_LAST_DEVICE, deviceName).apply()
        _connectionState.value = ObdConnectionState.Connected(deviceName)
        detectVehicle()
        readDtcs()
        checkItvReadiness()
        startPolling()
    }

    override suspend fun readDtcs(): DtcResult = withContext(Dispatchers.IO) {
        if (!bluetoothService.isConnected) return@withContext _dtcResult.value
        val mil = executor.parseMilOn(bluetoothService.sendCommand("0101"))
        val active = executor.parseDtcCodes(bluetoothService.sendCommand("03", delayMs = 350))
            .map { dtcDatabase.find(it) }
        val pending = executor.parseDtcCodes(bluetoothService.sendCommand("07", delayMs = 350))
            .map { dtcDatabase.find(it) }
        DtcResult(milOn = mil || active.isNotEmpty(), active = active, pending = pending).also {
            _dtcResult.value = it
        }
    }

    override suspend fun clearDtcs() {
        if (!bluetoothService.isConnected) return
        bluetoothService.sendCommand("04", delayMs = 500)
        _dtcResult.value = DtcResult()
    }

    override suspend fun readLivePids(): LiveData = withContext(Dispatchers.IO) {
        if (!bluetoothService.isConnected) return@withContext _liveData.value
        var snapshot = _liveData.value
        AutoDiagPid.specs.forEach { spec ->
            val response = bluetoothService.sendCommand(spec.command)
            val value = executor.parseLivePid(spec.pidHex, response)
            snapshot = snapshot.withPid(value)
        }
        _liveData.value = snapshot
        snapshot
    }

    override suspend fun checkItvReadiness(): List<ItvMonitor> = withContext(Dispatchers.IO) {
        val monitors = if (bluetoothService.isConnected) {
            executor.parseReadiness(bluetoothService.sendCommand("0101", delayMs = 250))
        } else {
            _itvMonitors.value
        }
        _itvMonitors.value = monitors
        monitors
    }

    override suspend fun readDpfStatus(): DpfStatus {
        val vehicle = _vehicleInfo.value
        val showDpf = vehicle.fuelType == FuelType.DIESEL || dieselFallback == true
        if (!showDpf) return DpfStatus(visible = false)

        val live = _liveData.value
        val engineLoad = live.value("04") ?: 0f
        val oilTemp = live.value("5C") ?: live.value("05") ?: 80f
        val load = ((engineLoad * 0.65f) + ((oilTemp - 60f).coerceAtLeast(0f) * 0.7f))
            .coerceIn(0f, 100f)
            .toInt()
        val regenActive = oilTemp > 95f && engineLoad > 35f
        val status = when {
            regenActive -> "Regeneración activa 🔥"
            load > 70 -> "DPF necesita regeneración ⚠️"
            else -> "DPF limpio ✅"
        }
        return DpfStatus(
            visible = true,
            loadPercent = load,
            regenActive = regenActive,
            statusTextEs = status,
            adviceEs = if (load > 70) {
                "Recomendamos conducir 20–30 min a velocidad constante en autovía para limpiar el DPF automáticamente"
            } else null,
            warningEs = if (load > 90) {
                "El DPF está muy saturado. Visita un taller antes de que se averíe el filtro (coste de sustitución: 800–2.000€)"
            } else null,
            exhaustTemperatureTrend = oilTempTrend.toList()
        )
    }

    override fun setDieselFallback(isDiesel: Boolean) {
        dieselFallback = isDiesel
        _vehicleInfo.value = _vehicleInfo.value.copy(fuelType = if (isDiesel) FuelType.DIESEL else FuelType.GASOLINE)
    }

    @SuppressLint("MissingPermission")
    private suspend fun retryConnection(deviceName: String): Result<Unit> {
        repeat(3) { attempt ->
            val result = runCatching {
                bluetoothService.connectByName(deviceName)
                bluetoothService.initializeElm()
            }
            if (result.isSuccess) return Result.success(Unit)
            delay((attempt + 1) * 500L)
        }
        return Result.failure(IllegalStateException("No connection after retries"))
    }

    private suspend fun detectVehicle() {
        if (!bluetoothService.isConnected) return
        val vin = runCatching { executor.parseVin(bluetoothService.sendCommand("0902", delayMs = 500)) }.getOrNull()
        val mileage = runCatching { executor.parseMileage(bluetoothService.sendCommand("01A6", delayMs = 300)) }.getOrNull()
        _vehicleInfo.value = VehicleInfo(
            vin = vin,
            make = vin?.let(::detectMake),
            model = null,
            year = vin?.let(::detectYear),
            fuelType = dieselFallback?.let { if (it) FuelType.DIESEL else FuelType.GASOLINE } ?: FuelType.UNKNOWN,
            mileageKm = mileage
        )
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            var index = 0
            while (isActive && bluetoothService.isConnected) {
                val spec = AutoDiagPid.specs[index % AutoDiagPid.specs.size]
                val parsed = runCatching {
                    executor.parseLivePid(spec.pidHex, bluetoothService.sendCommand(spec.command))
                }.getOrElse {
                    LivePid(spec.pidHex, spec.labelEs, spec.unit, supported = false)
                }
                _liveData.value = _liveData.value.withPid(parsed)
                parsed.value?.takeIf { spec.pidHex == "5C" }?.toInt()?.let { oilTemp ->
                    oilTempTrend.addLast(oilTemp)
                    while (oilTempTrend.size > 12) oilTempTrend.removeFirst()
                }
                index++
                delay(1_000L)
            }
        }
    }

    private fun LiveData.withPid(pid: LivePid): LiveData =
        copy(values = values + (pid.pidHex to pid))

    private fun detectMake(vin: String): String? = when (vin.take(3)) {
        "WVW", "WV1", "WV2" -> "Volkswagen"
        "VSS" -> "SEAT"
        "VF1" -> "Renault"
        "VF3" -> "Peugeot"
        "WBA" -> "BMW"
        "WDB" -> "Mercedes-Benz"
        "TMB" -> "Skoda"
        else -> null
    }

    private fun detectYear(vin: String): Int? {
        val yearCode = vin.getOrNull(9) ?: return null
        return when (yearCode) {
            'H' -> 2017
            'J' -> 2018
            'K' -> 2019
            'L' -> 2020
            'M' -> 2021
            'N' -> 2022
            'P' -> 2023
            'R' -> 2024
            'S' -> 2025
            'T' -> 2026
            else -> null
        }
    }

    private companion object {
        const val KEY_LAST_DEVICE = "last_device"
    }
}
