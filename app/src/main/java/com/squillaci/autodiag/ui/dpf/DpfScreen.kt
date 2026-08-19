package com.squillaci.autodiag.ui.dpf

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DpfScreen(viewModel: DpfViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Estado del DPF", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        if (uiState.askDiesel) {
            DieselQuestion(viewModel::setDiesel)
        } else if (!uiState.status.visible) {
            Text("Esta pantalla solo se muestra para vehículos diésel.", color = Color(0xFF667085))
        } else {
            Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(uiState.status.statusTextEs, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Carga estimada: ${uiState.status.loadPercent}%", color = Color(0xFF475467))
                    LinearProgressIndicator(
                        progress = { uiState.status.loadPercent / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (uiState.status.loadPercent > 90) Color(0xFFDC2626) else Color(0xFFD97706)
                    )
                    Sparkline(uiState.status.exhaustTemperatureTrend)
                }
            }
            uiState.status.adviceEs?.let { AdviceCard(it, Color(0xFFFFF3D6)) }
            uiState.status.warningEs?.let { AdviceCard(it, Color(0xFFFFE4E6)) }
        }
    }
}

@Composable
private fun DieselQuestion(onAnswer: (Boolean) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("¿Tu coche es diésel?", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("AutoDiag mostrará el DPF solo si tiene sentido para tu vehículo.", color = Color(0xFF667085))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { onAnswer(true) }, modifier = Modifier.weight(1f)) { Text("Sí") }
                OutlinedButton(onClick = { onAnswer(false) }, modifier = Modifier.weight(1f)) { Text("No") }
            }
        }
    }
}

@Composable
private fun AdviceCard(text: String, color: Color) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Text(text, modifier = Modifier.padding(18.dp), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Sparkline(values: List<Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Tendencia temperatura escape", fontWeight = FontWeight.SemiBold)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFFF2F4F7), RoundedCornerShape(14.dp))
                .padding(8.dp)
        ) {
            if (values.size < 2) return@Canvas
            val max = values.maxOrNull()?.toFloat() ?: 1f
            val min = values.minOrNull()?.toFloat() ?: 0f
            val range = (max - min).coerceAtLeast(1f)
            val step = size.width / (values.size - 1)
            values.zipWithNext().forEachIndexed { index, pair ->
                val y1 = size.height - ((pair.first - min) / range * size.height)
                val y2 = size.height - ((pair.second - min) / range * size.height)
                drawLine(
                    color = Color(0xFF2563EB),
                    start = Offset(index * step, y1),
                    end = Offset((index + 1) * step, y2),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
