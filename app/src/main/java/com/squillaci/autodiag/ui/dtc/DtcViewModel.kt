package com.squillaci.autodiag.ui.dtc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squillaci.autodiag.domain.model.DtcResult
import com.squillaci.autodiag.domain.repository.ObdRepository
import com.squillaci.autodiag.domain.usecase.ClearDtcsUseCase
import com.squillaci.autodiag.domain.usecase.ReadDtcsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DtcUiState(
    val result: DtcResult = DtcResult(),
    val clearing: Boolean = false
)

@HiltViewModel
class DtcViewModel @Inject constructor(
    repository: ObdRepository,
    private val readDtcsUseCase: ReadDtcsUseCase,
    private val clearDtcsUseCase: ClearDtcsUseCase
) : ViewModel() {
    val uiState: StateFlow<DtcUiState> = repository.dtcResult
        .map { DtcUiState(result = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DtcUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { readDtcsUseCase() }
    }

    fun clear() {
        viewModelScope.launch { clearDtcsUseCase() }
    }
}
