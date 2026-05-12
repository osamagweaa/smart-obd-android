package com.example.obdapp.domain.model

data class DtcInfo(
    val code: String,
    val titleEs: String,
    val descriptionEs: String,
    val severity: DtcSeverity,
    val estimatedRepairEuros: IntRange
)

enum class DtcSeverity {
    GRAVE,
    MODERADO,
    INFORMATIVO
}

data class DtcResult(
    val milOn: Boolean = false,
    val active: List<DtcInfo> = emptyList(),
    val pending: List<DtcInfo> = emptyList()
) {
    val allCodes: List<DtcInfo> = active + pending
}
