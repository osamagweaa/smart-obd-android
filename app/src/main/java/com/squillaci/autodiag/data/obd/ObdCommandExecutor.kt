package com.squillaci.autodiag.data.obd

import com.squillaci.autodiag.domain.model.AutoDiagPid
import com.squillaci.autodiag.domain.model.ItvMonitor
import com.squillaci.autodiag.domain.model.ItvMonitorStatus
import com.squillaci.autodiag.domain.model.LivePid
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObdCommandExecutor @Inject constructor() {
    fun parseLivePid(pidHex: String, rawResponse: String): LivePid {
        val spec = AutoDiagPid.specs.first { it.pidHex == pidHex.uppercase() }
        if (rawResponse.isNoData()) {
            return LivePid(spec.pidHex, spec.labelEs, spec.unit, value = null, supported = false)
        }
        val bytes = rawResponse.hexBytes()
        val index = bytes.windowed(2).indexOfFirst { it[0] == 0x41 && it[1] == spec.pidHex.toInt(16) }
        val value = if (index >= 0) calculatePidValue(pidHex, bytes.drop(index + 2)) else null
        return LivePid(spec.pidHex, spec.labelEs, spec.unit, value, supported = value != null)
    }

    fun parseDtcCodes(rawResponse: String): List<String> {
        if (rawResponse.isNoData()) return emptyList()
        return Regex("[PCBU][0-3][0-9A-F]{3}", RegexOption.IGNORE_CASE)
            .findAll(rawResponse)
            .map { it.value.uppercase() }
            .distinct()
            .toList()
    }

    fun parseMilOn(rawResponse: String): Boolean {
        val bytes = rawResponse.hexBytes()
        val index = bytes.windowed(2).indexOfFirst { it[0] == 0x41 && it[1] == 0x01 }
        return index >= 0 && bytes.getOrNull(index + 2)?.let { it and 0x80 != 0 } == true
    }

    fun parseReadiness(rawResponse: String): List<ItvMonitor> {
        val bytes = rawResponse.hexBytes()
        val index = bytes.windowed(2).indexOfFirst { it[0] == 0x41 && it[1] == 0x01 }
        if (index < 0 || bytes.size < index + 6) return defaultReadiness()
        val b = bytes.drop(index + 2).take(4)
        val incompleteMask = listOf(
            b[1] and 0x01,
            b[1] and 0x02,
            b[1] and 0x04,
            b[2] and 0x01,
            b[2] and 0x02,
            b[2] and 0x04,
            b[2] and 0x08,
            b[3] and 0x01,
            b[3] and 0x02,
            b[3] and 0x04
        )
        return monitorNames.mapIndexed { position, name ->
            val status = if (incompleteMask[position] == 0) ItvMonitorStatus.LISTO else ItvMonitorStatus.NO_LISTO
            ItvMonitor(position.toString(), name, status)
        }
    }

    fun parseVin(rawResponse: String): String? {
        val ascii = rawResponse.hexBytes()
            .filter { it in 32..126 }
            .map { it.toChar() }
            .joinToString("")
            .filter { it.isLetterOrDigit() }
        return Regex("[A-HJ-NPR-Z0-9]{17}").find(ascii)?.value
    }

    fun parseMileage(rawResponse: String): Int? {
        val bytes = rawResponse.hexBytes()
        val index = bytes.windowed(2).indexOfFirst { it[0] == 0x41 && it[1] == 0xA6 }
        return if (index >= 0) bytes.getOrNull(index + 2)?.times(256)?.plus(bytes.getOrNull(index + 3) ?: 0) else null
    }

    fun defaultReadiness(): List<ItvMonitor> = monitorNames.mapIndexed { index, name ->
        ItvMonitor(index.toString(), name, if (index < 8) ItvMonitorStatus.LISTO else ItvMonitorStatus.EN_PROCESO)
    }

    private fun calculatePidValue(pidHex: String, data: List<Int>): Float? {
        val a = data.getOrNull(0) ?: return null
        val b = data.getOrNull(1) ?: 0
        return when (pidHex.uppercase()) {
            "0C" -> ((a * 256) + b) / 4f
            "0D" -> a.toFloat()
            "05", "0F", "5C" -> (a - 40).toFloat()
            "11", "2F", "04" -> (a * 100f) / 255f
            else -> null
        }
    }

    private fun String.isNoData(): Boolean =
        contains("NO DATA", ignoreCase = true) || contains("UNABLE", ignoreCase = true) || isBlank()

    private fun String.hexBytes(): List<Int> =
        Regex("[0-9A-Fa-f]{2}")
            .findAll(this)
            .mapNotNull { it.value.toIntOrNull(16) }
            .toList()

    private val monitorNames = listOf(
        "Sistema de encendido",
        "Sistema de combustible",
        "Componentes principales",
        "Catalizador",
        "Catalizador calentado",
        "Sistema evaporativo",
        "Sistema de aire secundario",
        "Sensor de oxígeno",
        "Calentador sensor de oxígeno",
        "Sistema EGR"
    )
}
