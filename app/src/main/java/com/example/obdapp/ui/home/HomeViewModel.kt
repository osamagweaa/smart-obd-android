package com.example.obdapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obdapp.domain.model.LivePid
import com.example.obdapp.domain.model.ObdConnectionState
import com.example.obdapp.domain.model.VehicleInfo
import com.example.obdapp.domain.repository.ObdRepository
import com.example.obdapp.domain.usecase.ConnectObdUseCase
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
                liveData.values["0C"],
                liveData.values["05"],
                liveData.values["0D"]
            ).filter { it.supported && it.value != null }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch { connectObdUseCase.autoReconnect() }
    }
}
