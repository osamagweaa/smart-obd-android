package com.squillaci.autodiag.domain.model

data class DpfStatus(
    val visible: Boolean = false,
    val loadPercent: Int = 0,
    val regenActive: Boolean = false,
    val statusTextEs: String = "DPF limpio ✅",
    val adviceEs: String? = null,
    val warningEs: String? = null,
    val exhaustTemperatureTrend: List<Int> = emptyList()
)
