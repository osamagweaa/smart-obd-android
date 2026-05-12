package com.example.obdapp.domain.usecase

import com.example.obdapp.domain.model.DtcSeverity
import com.example.obdapp.domain.model.DtcResult
import com.example.obdapp.domain.model.LiveData
import com.example.obdapp.domain.model.SafetyVerdict
import com.example.obdapp.domain.model.SafetyVerdictLevel
import javax.inject.Inject

class EvaluateSafetyUseCase @Inject constructor() {
    operator fun invoke(liveData: LiveData, dtcResult: DtcResult): SafetyVerdict {
        val coolantTemp = liveData.value("05")
        val engineLoad = liveData.value("04")
        val rpm = liveData.value("0C")
        val fuel = liveData.value("2F")
        val oilTemp = liveData.value("5C")
        val criticalPids = listOf("05", "04", "2F", "5C", "0C")
            .mapNotNull { liveData.values[it] }
            .filter { it.value != null }
            .take(3)

        val hasSevereDtc = dtcResult.allCodes.any { it.severity == DtcSeverity.GRAVE }
        val hasModerateDtc = dtcResult.allCodes.any { it.severity == DtcSeverity.MODERADO }
        val highLoadAtIdle = engineLoad != null && engineLoad > 95f && (rpm ?: 0f) in 500f..1100f

        return when {
            coolantTemp != null && coolantTemp > 110f -> SafetyVerdict(
                SafetyVerdictLevel.RED,
                "NO recomendado — para el motor",
                "La temperatura del motor es demasiado alta. Detén el coche en un lugar seguro, apaga el motor y espera a que se enfríe antes de continuar.",
                criticalPids
            )
            hasSevereDtc -> SafetyVerdict(
                SafetyVerdictLevel.RED,
                "NO recomendado — para el motor",
                "Hay una avería importante registrada. Circular así puede aumentar el daño y el coste de reparación, por lo que conviene llamar a asistencia o ir al taller con prudencia.",
                criticalPids
            )
            highLoadAtIdle -> SafetyVerdict(
                SafetyVerdictLevel.RED,
                "NO recomendado — para el motor",
                "El motor está trabajando demasiado mientras el coche está parado. Es una señal de riesgo y no recomendamos seguir conduciendo hasta revisarlo.",
                criticalPids
            )
            hasModerateDtc -> SafetyVerdict(
                SafetyVerdictLevel.AMBER,
                "Conduce con precaución",
                "Hay una avería moderada. Puedes mover el coche si responde con normalidad, pero evita trayectos largos y pide cita en el taller.",
                criticalPids
            )
            fuel != null && fuel < 10f -> SafetyVerdict(
                SafetyVerdictLevel.AMBER,
                "Conduce con precaución",
                "El nivel de combustible es muy bajo. Reposta cuanto antes para evitar quedarte parado o dañar componentes del sistema de combustible.",
                criticalPids
            )
            oilTemp != null && oilTemp > 130f -> SafetyVerdict(
                SafetyVerdictLevel.AMBER,
                "Conduce con precaución",
                "La temperatura del aceite es alta. Reduce la carga del motor, evita acelerones y comprueba de nuevo tras unos minutos.",
                criticalPids
            )
            else -> SafetyVerdict(
                SafetyVerdictLevel.GREEN,
                "SÍ, puedes conducir",
                "No vemos señales críticas en los datos actuales. Conduce con normalidad y vuelve a comprobar si aparece una luz de aviso.",
                criticalPids
            )
        }
    }
}
