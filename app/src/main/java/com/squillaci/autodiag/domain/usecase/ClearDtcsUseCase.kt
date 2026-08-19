package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.repository.ObdRepository
import javax.inject.Inject

class ClearDtcsUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke() = repository.clearDtcs()
}
