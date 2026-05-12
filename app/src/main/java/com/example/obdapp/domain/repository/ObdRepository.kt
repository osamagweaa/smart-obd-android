package com.example.obdapp.domain.repository

import com.example.obdapp.domain.model.DpfStatus
import com.example.obdapp.domain.model.DtcResult
import com.example.obdapp.domain.model.ItvMonitor
import com.example.obdapp.domain.model.LiveData
import com.example.obdapp.domain.model.ObdConnectionState
import com.example.obdapp.domain.model.VehicleInfo
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
