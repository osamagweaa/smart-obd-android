package com.example.obdapp.domain

import com.example.obdapp.domain.model.DtcInfo
import com.example.obdapp.domain.model.DtcResult
import com.example.obdapp.domain.model.DtcSeverity
import com.example.obdapp.domain.model.LiveData
import com.example.obdapp.domain.model.LivePid
import com.example.obdapp.domain.model.SafetyVerdictLevel
import com.example.obdapp.domain.usecase.EvaluateSafetyUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class EvaluateSafetyUseCaseTest {
    private val useCase = EvaluateSafetyUseCase()

    @Test
    fun severeDtcReturnsRed() {
        val result = useCase(
            liveData = LiveData.Empty,
            dtcResult = DtcResult(active = listOf(dtc(DtcSeverity.GRAVE)))
        )

        assertEquals(SafetyVerdictLevel.RED, result.level)
    }

    @Test
    fun lowFuelReturnsAmber() {
        val result = useCase(
            liveData = LiveData.Empty.copy(
                values = LiveData.Empty.values + ("2F" to LivePid("2F", "Nivel de combustible", "%", 7f))
            ),
            dtcResult = DtcResult()
        )

        assertEquals(SafetyVerdictLevel.AMBER, result.level)
    }

    @Test
    fun healthyDataReturnsGreen() {
        val result = useCase(
            liveData = LiveData.Empty.copy(
                values = LiveData.Empty.values + mapOf(
                    "05" to LivePid("05", "Temperatura motor", "°C", 88f),
                    "04" to LivePid("04", "Carga motor", "%", 25f),
                    "2F" to LivePid("2F", "Nivel de combustible", "%", 40f),
                    "5C" to LivePid("5C", "Temperatura aceite", "°C", 95f)
                )
            ),
            dtcResult = DtcResult()
        )

        assertEquals(SafetyVerdictLevel.GREEN, result.level)
    }

    private fun dtc(severity: DtcSeverity) = DtcInfo(
        code = "P0300",
        titleEs = "Fallo de encendido",
        descriptionEs = "Descripción",
        severity = severity,
        estimatedRepairEuros = 100..300
    )
}
