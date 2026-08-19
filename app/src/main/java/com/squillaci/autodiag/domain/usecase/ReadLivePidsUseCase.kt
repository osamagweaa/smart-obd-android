package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.model.LiveData
import com.squillaci.autodiag.domain.repository.ObdRepository
import javax.inject.Inject

class ReadLivePidsUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): LiveData = repository.readLivePids()
}
