package com.squillaci.autodiag.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squillaci.autodiag.domain.model.LivePid
import com.squillaci.autodiag.domain.model.ObdConnectionState
import com.squillaci.autodiag.domain.model.VehicleInfo
import com.squillaci.autodiag.domain.repository.ObdRepository
import com.squillaci.autodiag.domain.usecase.ConnectObdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val connectionState: ObdConnectionState = ObdConnectionState.Idle,
    val vehicleInfo: VehicleInfo = VehicleInfo(),
    val stripPids: List<LivePid> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: ObdRepository,
    private val connectObdUseCase: ConnectObdUseCase
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        repository.connectionState,
        repository.vehicleInfo,
        repository.liveData
    ) { connection, vehicle, liveData ->
        HomeUiState(
            connectionState = connection,
            vehicleInfo = vehicle,
            stripPids = listOfNotNull(
                liveData.values["0C"], // RPM
                liveData.values["05"], // Temp
                liveData.values["0D"], // Speed
                liveData.values["04"], // Load
                liveData.values["2F"]  // Fuel
            ).filter { it.supported && it.value != null }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch { connectObdUseCase.autoReconnect() }
    }
}
