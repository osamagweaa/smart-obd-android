package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.repository.ObdRepository
import javax.inject.Inject

class ConnectObdUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(deviceName: String) {
        require(deviceName.isNotBlank()) { "El dispositivo Bluetooth no puede estar vacío" }
        repository.connectToDevice(deviceName)
    }

    suspend fun autoReconnect() = repository.autoReconnect()
}
