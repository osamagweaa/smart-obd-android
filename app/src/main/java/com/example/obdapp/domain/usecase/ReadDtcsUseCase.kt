package com.example.obdapp.domain.usecase

import com.example.obdapp.domain.model.DtcResult
import com.example.obdapp.domain.repository.ObdRepository
import javax.inject.Inject

class ReadDtcsUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): DtcResult = repository.readDtcs()
}
