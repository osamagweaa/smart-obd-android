package com.example.obdapp.domain.usecase

import com.example.obdapp.domain.repository.ObdRepository
import javax.inject.Inject

class ClearDtcsUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke() = repository.clearDtcs()
}
