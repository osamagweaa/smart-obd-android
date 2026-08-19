package com.squillaci.autodiag.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squillaci.autodiag.domain.usecase.GenerateReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val reportText: String = "Generando informe..."
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val generateReportUseCase: GenerateReportUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ReportUiState(generateReportUseCase())
        }
    }
}
