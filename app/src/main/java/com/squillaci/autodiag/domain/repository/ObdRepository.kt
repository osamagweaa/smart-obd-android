package com.squillaci.autodiag.domain.repository

import com.squillaci.autodiag.domain.model.DpfStatus
import com.squillaci.autodiag.domain.model.DtcResult
import com.squillaci.autodiag.domain.model.ItvMonitor
import com.squillaci.autodiag.domain.model.LiveData
import com.squillaci.autodiag.domain.model.ObdConnectionState
import com.squillaci.autodiag.domain.model.VehicleInfo
import kotlinx.coroutines.flow.StateFlow

interface ObdRepository {
    val connectionState: StateFlow<ObdConnectionState>
    val vehicleInfo: StateFlow<VehicleInfo>
    val liveData: StateFlow<LiveData>
    val dtcResult: StateFlow<DtcResult>
    val itvMonitors: StateFlow<List<ItvMonitor>>

    suspend fun autoReconnect()
    suspend fun connectToDevice(deviceName: String)
    suspend fun readDtcs(): DtcResult
    suspend fun clearDtcs()
    suspend fun readLivePids(): LiveData
    suspend fun checkItvReadiness(): List<ItvMonitor>
    suspend fun readDpfStatus(): DpfStatus
    fun setDieselFallback(isDiesel: Boolean)
}
