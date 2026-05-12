package com.example.obdapp.domain.model

sealed class ObdConnectionState {
    data object Idle : ObdConnectionState()
    data object Connecting : ObdConnectionState()
    data class Connected(val deviceName: String) : ObdConnectionState()
    data object Disconnected : ObdConnectionState()
    data class Error(val messageEs: String) : ObdConnectionState()
}
