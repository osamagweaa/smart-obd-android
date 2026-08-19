package com.squillaci.autodiag.data.repository

import com.squillaci.autodiag.domain.model.DpfStatus
import com.squillaci.autodiag.domain.model.DtcInfo
import com.squillaci.autodiag.domain.model.DtcResult
import com.squillaci.autodiag.domain.model.DtcSeverity
import com.squillaci.autodiag.domain.model.FuelType
import com.squillaci.autodiag.domain.model.ItvMonitor
import com.squillaci.autodiag.domain.model.ItvMonitorStatus
import com.squillaci.autodiag.domain.model.LiveData
import com.squillaci.autodiag.domain.model.LivePid
import com.squillaci.autodiag.domain.model.ObdConnectionState
import com.squillaci.autodiag.domain.model.VehicleInfo
import com.squillaci.autodiag.domain.repository.ObdRepository
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
import kotlin.random.Random

@Singleton
class MockObdRepositoryImpl @Inject constructor() : ObdRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var pollingJob: Job? = null

    private val _connectionState = MutableStateFlow<ObdConnectionState>(ObdConnectionState.Idle)
    override val connectionState: StateFlow<ObdConnectionState> = _connectionState

    private val _vehicleInfo = MutableStateFlow(VehicleInfo(
        vin = "WBA1234567890DEMO",
        make = "BMW",
        model = "Serie 3 (Demo)",
        year = 2021,
        fuelType = FuelType.GASOLINE,
        mileageKm = 45200
    ))
    override val vehicleInfo: StateFlow<VehicleInfo> = _vehicleInfo

    private val _liveData = MutableStateFlow(LiveData.Empty)
    override val liveData: StateFlow<LiveData> = _liveData

    private val _dtcResult = MutableStateFlow(DtcResult())
    override val dtcResult: StateFlow<DtcResult> = _dtcResult

    private val _itvMonitors = MutableStateFlow<List<ItvMonitor>>(emptyList())
    override val itvMonitors: StateFlow<List<ItvMonitor>> = _itvMonitors

    override suspend fun autoReconnect() {
        connectToDevice("Demo ELM327")
    }

    override suspend fun connectToDevice(deviceName: String) {
        _connectionState.value = ObdConnectionState.Connecting
        delay(1500)
        _connectionState.value = ObdConnectionState.Connected(deviceName)
        readDtcs()
        startPolling()
    }

    override suspend fun readDtcs(): DtcResult {
        delay(1000)
        val result = DtcResult(
            milOn = true,
            active = listOf(
                DtcInfo(
                    code = "P0300",
                    titleEs = "Fallo de encendido detectado",
                    descriptionEs = "Se han detectado fallos de encendido en varios cilindros. Esto puede causar daños al catalizador si no se repara.",
                    severity = DtcSeverity.GRAVE,
                    estimatedRepairEuros = 50..250
                )
            ),
            pending = emptyList()
        )
        _dtcResult.value = result
        return result
    }

    override suspend fun clearDtcs() {
        delay(2000)
        _dtcResult.value = DtcResult()
    }

    override suspend fun readLivePids(): LiveData {
        return _liveData.value
    }

    override suspend fun checkItvReadiness(): List<ItvMonitor> {
        delay(800)
        val monitors = listOf(
            ItvMonitor("MIL", "Check Engine", ItvMonitorStatus.LISTO),
            ItvMonitor("MIS", "Misfire", ItvMonitorStatus.LISTO),
            ItvMonitor("FUE", "Fuel System", ItvMonitorStatus.LISTO)
        )
        _itvMonitors.value = monitors
        return monitors
    }

    override suspend fun readDpfStatus(): DpfStatus {
        return DpfStatus(visible = true, loadPercent = 35, statusTextEs = "Limpio (Demo)")
    }

    override fun setDieselFallback(isDiesel: Boolean) {
        _vehicleInfo.value = _vehicleInfo.value.copy(fuelType = if (isDiesel) FuelType.DIESEL else FuelType.GASOLINE)
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                val rpmValue = 800f + Random.nextFloat() * 100f
                val speedValue = 0f + Random.nextFloat() * 5f
                // Simulate HIGH temperature (115°C) to trigger RED warning in SafeTodayScreen
                val coolantValue = 115f + Random.nextFloat() * 2f
                val loadValue = 25f + Random.nextFloat() * 10f
                val fuelValue = 45f + Random.nextFloat() * 5f

                val newLiveData = LiveData(
                    values = mapOf(
                        "0C" to LivePid("0C", "RPM", "rpm", rpmValue, true),
                        "0D" to LivePid("0D", "Velocidad", "km/h", speedValue, true),
                        "05" to LivePid("05", "Temp. Refrigerante", "°C", coolantValue, true),
                        "04" to LivePid("04", "Carga Motor", "%", loadValue, true),
                        "2F" to LivePid("2F", "Nivel Combustible", "%", fuelValue, true)
                    )
                )
                _liveData.value = newLiveData
                delay(1000)
            }
        }
    }
}
