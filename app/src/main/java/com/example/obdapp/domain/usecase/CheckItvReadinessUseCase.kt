package com.example.obdapp.domain.usecase

import com.example.obdapp.domain.model.ItvMonitor
import com.example.obdapp.domain.repository.ObdRepository
import javax.inject.Inject

class CheckItvReadinessUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): List<ItvMonitor> = repository.checkItvReadiness()
}
