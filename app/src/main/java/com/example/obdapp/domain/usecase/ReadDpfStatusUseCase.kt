package com.example.obdapp.domain.usecase

import com.example.obdapp.domain.model.DpfStatus
import com.example.obdapp.domain.repository.ObdRepository
import javax.inject.Inject

class ReadDpfStatusUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): DpfStatus = repository.readDpfStatus()
    fun setDieselFallback(isDiesel: Boolean) = repository.setDieselFallback(isDiesel)
}
