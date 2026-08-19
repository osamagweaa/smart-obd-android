package com.squillaci.autodiag.domain.usecase

import com.squillaci.autodiag.domain.model.DtcResult
import com.squillaci.autodiag.domain.model.ItvMonitorStatus
import com.squillaci.autodiag.domain.model.LiveData
import com.squillaci.autodiag.domain.model.VehicleInfo
import com.squillaci.autodiag.domain.repository.ObdRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GenerateReportUseCase @Inject constructor(
    private val repository: ObdRepository
) {
    suspend operator fun invoke(): String {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")).format(Date())
        val vehicleInfo = repository.vehicleInfo.value
        val dtcs = repository.readDtcs()
        val liveData = repository.readLivePids()
        val monitors = repository.checkItvReadiness()

        return buildString {
            appendLine("Informe de diagnóstico generado con AutoDiag — $date")
            appendLine("Para uso informativo. Consulta siempre a un mecánico certificado.")
            appendLine()
            appendVehicle(vehicleInfo)
            appendDtcs("Averías activas", dtcs.active)
            appendDtcs("Averías pendientes", dtcs.pending)
            appendLiveData(liveData)
            appendLine("Revisión ITV")
            monitors.forEach { monitor ->
                val status = when (monitor.status) {
                    ItvMonitorStatus.LISTO -> "Listo"
                    ItvMonitorStatus.EN_PROCESO -> "En proceso"
                    ItvMonitorStatus.NO_LISTO -> "No listo"
                }
                appendLine("- ${monitor.nameEs}: $status")
            }
        }
    }

    private fun StringBuilder.appendVehicle(vehicleInfo: VehicleInfo) {
        appendLine("Vehículo")
        appendLine("- Nombre: ${vehicleInfo.displayName}")
        appendLine("- VIN: ${vehicleInfo.vin ?: "No disponible"}")
        appendLine("- Kilometraje: ${vehicleInfo.mileageKm?.let { "$it km" } ?: "No disponible"}")
        appendLine()
    }

    private fun StringBuilder.appendDtcs(title: String, dtcs: List<com.squillaci.autodiag.domain.model.DtcInfo>) {
        appendLine(title)
        if (dtcs.isEmpty()) {
            appendLine("- Sin registros")
        } else {
            dtcs.forEach { dtc ->
                appendLine("- ${dtc.code}: ${dtc.titleEs} (${dtc.severity.name.lowercase(Locale("es", "ES"))})")
                appendLine("  ${dtc.descriptionEs}")
                appendLine("  Coste estimado: ${dtc.estimatedRepairEuros.first}-${dtc.estimatedRepairEuros.last}€")
            }
        }
        appendLine()
    }

    private fun StringBuilder.appendLiveData(liveData: LiveData) {
        appendLine("Datos en vivo")
        val values = liveData.visibleValues()
        if (values.isEmpty()) {
            appendLine("- No hay datos compatibles en este momento")
        } else {
            values.forEach { pid ->
                appendLine("- ${pid.labelEs}: ${pid.displayValue} ${pid.unit}")
            }
        }
        appendLine()
    }
}
