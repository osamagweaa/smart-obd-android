package com.squillaci.autodiag.ui.itv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squillaci.autodiag.domain.model.ItvMonitor
import com.squillaci.autodiag.domain.model.ItvMonitorStatus
import com.squillaci.autodiag.domain.repository.ObdRepository
import com.squillaci.autodiag.domain.usecase.CheckItvReadinessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItvUiState(
    val monitors: List<ItvMonitor> = emptyList()
) {
    val incompleteCount: Int = monitors.count { it.status != ItvMonitorStatus.LISTO }
    val readyForItv: Boolean = incompleteCount <= 2
}

@HiltViewModel
class ItvViewModel @Inject constructor(
    repository: ObdRepository,
    private val checkItvReadinessUseCase: CheckItvReadinessUseCase
) : ViewModel() {
    val uiState: StateFlow<ItvUiState> = repository.itvMonitors
        .map { ItvUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ItvUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { checkItvReadinessUseCase() }
    }
}
