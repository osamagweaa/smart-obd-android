package com.example.obdapp.domain.usecase

import com.example.obdapp.domain.model.LiveData
import com.example.obdapp.domain.repository.ObdRepository
import javax.inject.Inject

class ReadLivePidsUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): LiveData = repository.readLivePids()
}
