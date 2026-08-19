package com.squillaci.autodiag.ui.safetoday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squillaci.autodiag.domain.model.SafetyVerdict
import com.squillaci.autodiag.domain.model.SafetyVerdictLevel
import com.squillaci.autodiag.domain.repository.ObdRepository
import com.squillaci.autodiag.domain.usecase.EvaluateSafetyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SafeTodayUiState(
    val verdict: SafetyVerdict = SafetyVerdict(
        SafetyVerdictLevel.GREEN,
        "SÍ, puedes conducir",
        "Conecta el adaptador para evaluar el coche con datos reales.",
        emptyList()
    )
)

@HiltViewModel
class SafeTodayViewModel @Inject constructor(
    repository: ObdRepository,
    evaluateSafetyUseCase: EvaluateSafetyUseCase
) : ViewModel() {
    val uiState: StateFlow<SafeTodayUiState> = combine(
        repository.liveData,
        repository.dtcResult
    ) { liveData, dtcResult ->
        SafeTodayUiState(evaluateSafetyUseCase(liveData, dtcResult))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SafeTodayUiState())
}
