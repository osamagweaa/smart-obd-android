package com.squillaci.autodiag.domain.model

data class LivePid(
    val pidHex: String,
    val labelEs: String,
    val unit: String,
    val value: Float? = null,
    val supported: Boolean = true
) {
    val displayValue: String
        get() = value?.let {
            if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it)
        } ?: "--"
}

data class LiveData(
    val values: Map<String, LivePid> = emptyMap()
) {
    fun value(pidHex: String): Float? = values[pidHex.uppercase()]?.value

    fun visibleValues(): List<LivePid> = values.values.filter { it.supported && it.value != null }

    companion object {
        val Empty = LiveData(
            AutoDiagPid.specs.associate { spec ->
                spec.pidHex to LivePid(spec.pidHex, spec.labelEs, spec.unit, null, supported = true)
            }
        )
    }
}

data class PidSpec(
    val pidHex: String,
    val command: String,
    val labelEs: String,
    val unit: String
)

object AutoDiagPid {
    val specs = listOf(
        PidSpec("0C", "010C", "Revoluciones", "RPM"),
        PidSpec("0D", "010D", "Velocidad", "km/h"),
        PidSpec("05", "0105", "Temperatura motor", "°C"),
        PidSpec("0F", "010F", "Temperatura aire entrada", "°C"),
        PidSpec("11", "0111", "Posición acelerador", "%"),
        PidSpec("2F", "012F", "Nivel de combustible", "%"),
        PidSpec("04", "0104", "Carga motor", "%"),
        PidSpec("5C", "015C", "Temperatura aceite", "°C")
    )
}
