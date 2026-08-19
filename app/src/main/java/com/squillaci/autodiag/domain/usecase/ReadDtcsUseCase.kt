package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.model.DtcResult
import com.squillaci.autodiag.domain.repository.ObdRepository
import javax.inject.Inject

class ReadDtcsUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): DtcResult = repository.readDtcs()
}
