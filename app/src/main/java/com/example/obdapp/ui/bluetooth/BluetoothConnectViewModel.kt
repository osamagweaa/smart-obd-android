package com.example.obdapp.ui.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obdapp.domain.model.ObdConnectionState
import com.example.obdapp.domain.repository.ObdRepository
import com.example.obdapp.domain.usecase.ConnectObdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BluetoothConnectUiState(
    val connectionState: ObdConnectionState = ObdConnectionState.Idle
)

@HiltViewModel
class BluetoothConnectViewModel @Inject constructor(
    repository: ObdRepository,
    private val connectObdUseCase: ConnectObdUseCase
) : ViewModel() {
    private var lastDevice: String? = null

    val uiState: StateFlow<BluetoothConnectUiState> = repository.connectionState
        .map { BluetoothConnectUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BluetoothConnectUiState())

    fun connect(deviceNameOrAddress: String) {
        lastDevice = deviceNameOrAddress
        viewModelScope.launch { connectObdUseCase(deviceNameOrAddress) }
    }

    fun retry() {
        lastDevice?.let(::connect)
    }
}
