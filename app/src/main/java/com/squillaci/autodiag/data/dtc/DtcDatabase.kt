package com.squillaci.autodiag.data.dtc

import android.content.Context
import com.squillaci.autodiag.R
import com.squillaci.autodiag.domain.model.DtcInfo
import com.squillaci.autodiag.domain.model.DtcSeverity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DtcDatabase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val codes: Map<String, DtcInfo> by lazy {
        listOf(
            entry("P0100", R.string.dtc_p0100_title, R.string.dtc_p0100_desc, DtcSeverity.MODERADO, 90..280),
            entry("P0101", R.string.dtc_p0101_title, R.string.dtc_p0101_desc, DtcSeverity.MODERADO, 90..300),
            entry("P0102", R.string.dtc_p0102_title, R.string.dtc_p0102_desc, DtcSeverity.MODERADO, 80..260),
            entry("P0113", R.string.dtc_p0113_title, R.string.dtc_p0113_desc, DtcSeverity.MODERADO, 50..180),
            entry("P0128", R.string.dtc_p0128_title, R.string.dtc_p0128_desc, DtcSeverity.MODERADO, 80..250),
            entry("P0130", R.string.dtc_p0130_title, R.string.dtc_p0130_desc, DtcSeverity.MODERADO, 100..280),
            entry("P0133", R.string.dtc_p0133_title, R.string.dtc_p0133_desc, DtcSeverity.MODERADO, 100..280),
            entry("P0135", R.string.dtc_p0135_title, R.string.dtc_p0135_desc, DtcSeverity.MODERADO, 90..260),
            entry("P0141", R.string.dtc_p0141_title, R.string.dtc_p0141_desc, DtcSeverity.MODERADO, 90..260),
            entry("P0171", R.string.dtc_p0171_title, R.string.dtc_p0171_desc, DtcSeverity.MODERADO, 80..350),
            entry("P0172", R.string.dtc_p0172_title, R.string.dtc_p0172_desc, DtcSeverity.MODERADO, 80..350),
            entry("P0201", R.string.dtc_p0201_title, R.string.dtc_p0201_desc, DtcSeverity.GRAVE, 120..400),
            entry("P0202", R.string.dtc_p0202_title, R.string.dtc_p0202_desc, DtcSeverity.GRAVE, 120..400),
            entry("P0203", R.string.dtc_p0203_title, R.string.dtc_p0203_desc, DtcSeverity.GRAVE, 120..400),
            entry("P0204", R.string.dtc_p0204_title, R.string.dtc_p0204_desc, DtcSeverity.GRAVE, 120..400),
            entry("P0300", R.string.dtc_p0300_title, R.string.dtc_p0300_desc, DtcSeverity.GRAVE, 150..400),
            entry("P0301", R.string.dtc_p0301_title, R.string.dtc_p0301_desc, DtcSeverity.GRAVE, 100..350),
            entry("P0302", R.string.dtc_p0302_title, R.string.dtc_p0302_desc, DtcSeverity.GRAVE, 100..350),
            entry("P0303", R.string.dtc_p0303_title, R.string.dtc_p0303_desc, DtcSeverity.GRAVE, 100..350),
            entry("P0304", R.string.dtc_p0304_title, R.string.dtc_p0304_desc, DtcSeverity.GRAVE, 100..350),
            entry("P0325", R.string.dtc_p0325_title, R.string.dtc_p0325_desc, DtcSeverity.MODERADO, 80..260),
            entry("P0335", R.string.dtc_p0335_title, R.string.dtc_p0335_desc, DtcSeverity.GRAVE, 120..350),
            entry("P0340", R.string.dtc_p0340_title, R.string.dtc_p0340_desc, DtcSeverity.GRAVE, 120..350),
            entry("P0401", R.string.dtc_p0401_title, R.string.dtc_p0401_desc, DtcSeverity.MODERADO, 120..450),
            entry("P0402", R.string.dtc_p0402_title, R.string.dtc_p0402_desc, DtcSeverity.MODERADO, 120..450),
            entry("P0420", R.string.dtc_p0420_title, R.string.dtc_p0420_desc, DtcSeverity.GRAVE, 400..1200),
            entry("P0430", R.string.dtc_p0430_title, R.string.dtc_p0430_desc, DtcSeverity.GRAVE, 400..1200),
            entry("P0440", R.string.dtc_p0440_title, R.string.dtc_p0440_desc, DtcSeverity.INFORMATIVO, 40..220),
            entry("P0441", R.string.dtc_p0441_title, R.string.dtc_p0441_desc, DtcSeverity.INFORMATIVO, 40..220),
            entry("P0442", R.string.dtc_p0442_title, R.string.dtc_p0442_desc, DtcSeverity.INFORMATIVO, 30..180),
            entry("P0455", R.string.dtc_p0455_title, R.string.dtc_p0455_desc, DtcSeverity.INFORMATIVO, 40..220),
            entry("P0456", R.string.dtc_p0456_title, R.string.dtc_p0456_desc, DtcSeverity.INFORMATIVO, 30..180),
            entry("P0500", R.string.dtc_p0500_title, R.string.dtc_p0500_desc, DtcSeverity.MODERADO, 80..280),
            entry("P0505", R.string.dtc_p0505_title, R.string.dtc_p0505_desc, DtcSeverity.MODERADO, 90..280),
            entry("P0562", R.string.dtc_p0562_title, R.string.dtc_p0562_desc, DtcSeverity.MODERADO, 80..300),
            entry("P0606", R.string.dtc_p0606_title, R.string.dtc_p0606_desc, DtcSeverity.GRAVE, 250..900),
            entry("P0700", R.string.dtc_p0700_title, R.string.dtc_p0700_desc, DtcSeverity.GRAVE, 150..900),
            entry("P0715", R.string.dtc_p0715_title, R.string.dtc_p0715_desc, DtcSeverity.GRAVE, 180..700),
            entry("P0740", R.string.dtc_p0740_title, R.string.dtc_p0740_desc, DtcSeverity.GRAVE, 250..1200),
            entry("P1120", R.string.dtc_p1120_title, R.string.dtc_p1120_desc, DtcSeverity.MODERADO, 120..380),
            entry("P1135", R.string.dtc_p1135_title, R.string.dtc_p1135_desc, DtcSeverity.MODERADO, 120..320),
            entry("P1345", R.string.dtc_p1345_title, R.string.dtc_p1345_desc, DtcSeverity.GRAVE, 180..500),
            entry("P1351", R.string.dtc_p1351_title, R.string.dtc_p1351_desc, DtcSeverity.MODERADO, 80..300),
            entry("P2002", R.string.dtc_p2002_title, R.string.dtc_p2002_desc, DtcSeverity.GRAVE, 250..1200),
            entry("P2004", R.string.dtc_p2004_title, R.string.dtc_p2004_desc, DtcSeverity.MODERADO, 150..500),
            entry("P2015", R.string.dtc_p2015_title, R.string.dtc_p2015_desc, DtcSeverity.MODERADO, 150..550),
            entry("P2187", R.string.dtc_p2187_title, R.string.dtc_p2187_desc, DtcSeverity.MODERADO, 80..350),
            entry("P2195", R.string.dtc_p2195_title, R.string.dtc_p2195_desc, DtcSeverity.MODERADO, 100..300),
            entry("P2263", R.string.dtc_p2263_title, R.string.dtc_p2263_desc, DtcSeverity.GRAVE, 250..1300),
            entry("P242F", R.string.dtc_p242f_title, R.string.dtc_p242f_desc, DtcSeverity.GRAVE, 300..2000)
        ).associateBy { it.code }
    }

    fun find(code: String): DtcInfo =
        codes[code.uppercase()] ?: fallbackInfo(code.uppercase())

    private fun entry(
        code: String,
        titleRes: Int,
        descriptionRes: Int,
        severity: DtcSeverity,
        range: IntRange
    ) = DtcInfo(code, context.getString(titleRes), context.getString(descriptionRes), severity, range)

    private fun fallbackInfo(code: String): DtcInfo {
        val severity = classify(code)
        return DtcInfo(
            code = code,
            titleEs = context.getString(R.string.dtc_unknown_title),
            descriptionEs = context.getString(R.string.dtc_unknown_desc),
            severity = severity,
            estimatedRepairEuros = when (severity) {
                DtcSeverity.GRAVE -> 150..900
                DtcSeverity.MODERADO -> 80..350
                DtcSeverity.INFORMATIVO -> 30..180
            }
        )
    }

    private fun classify(code: String): DtcSeverity = when {
        Regex("^P03(0[0-9]|1[0-2])$").matches(code) -> DtcSeverity.GRAVE
        code.startsWith("P044") || code.startsWith("P045") -> DtcSeverity.INFORMATIVO
        code.startsWith("P04") || code.startsWith("P01") || code.startsWith("P11") -> DtcSeverity.MODERADO
        code.startsWith("P07") -> DtcSeverity.GRAVE
        else -> DtcSeverity.INFORMATIVO
    }
}
