package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.model.DpfStatus
import com.squillaci.autodiag.domain.repository.ObdRepository
import javax.inject.Inject

class ReadDpfStatusUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): DpfStatus = repository.readDpfStatus()
    fun setDieselFallback(isDiesel: Boolean) = repository.setDieselFallback(isDiesel)
}
