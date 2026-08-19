package com.squillaci.autodiag.ui.dpf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squillaci.autodiag.domain.model.DpfStatus
import com.squillaci.autodiag.domain.usecase.ReadDpfStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DpfUiState(
    val status: DpfStatus = DpfStatus(),
    val askDiesel: Boolean = true
)

@HiltViewModel
class DpfViewModel @Inject constructor(
    private val readDpfStatusUseCase: ReadDpfStatusUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DpfUiState())
    val uiState: StateFlow<DpfUiState> = _uiState

    init {
        refresh()
    }

    fun setDiesel(isDiesel: Boolean) {
        readDpfStatusUseCase.setDieselFallback(isDiesel)
        refresh(askDiesel = false)
    }

    fun refresh(askDiesel: Boolean = _uiState.value.askDiesel) {
        viewModelScope.launch {
            val status = readDpfStatusUseCase()
            _uiState.value = DpfUiState(status = status, askDiesel = askDiesel && !status.visible)
        }
    }
}
