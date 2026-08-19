package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.model.ItvMonitor
import com.squillaci.autodiag.domain.repository.ObdRepository
import javax.inject.Inject

class CheckItvReadinessUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): List<ItvMonitor> = repository.checkItvReadiness()
}
