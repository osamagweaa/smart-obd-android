package com.squillaci.autodiag.domain.model

data class SafetyVerdict(
    val level: SafetyVerdictLevel,
    val titleEs: String,
    val explanationEs: String,
    val criticalPids: List<LivePid>
)

enum class SafetyVerdictLevel {
    GREEN,
    AMBER,
    RED
}
