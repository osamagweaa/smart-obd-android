package com.squillaci.autodiag.geminianalysis

import com.squillaci.autodiag.ui.dtc.LegacyDtcInfo

data class ObdData(
    val speed: String,
    val rpm: String,
    val coolantTemp: String,
    val voltage : String,
    val intakeTemp : String,
    val throttlePosition: String,
    val dtcCodes : List<LegacyDtcInfo>
)
