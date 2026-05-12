package com.example.obdapp.domain.model

data class ItvMonitor(
    val id: String,
    val nameEs: String,
    val status: ItvMonitorStatus
)

enum class ItvMonitorStatus {
    LISTO,
    EN_PROCESO,
    NO_LISTO
}
