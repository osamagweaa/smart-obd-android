package com.squillaci.autodiag.ui.itv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.squillaci.autodiag.domain.model.ItvMonitorStatus

@Composable
fun ItvScreen(viewModel: ItvViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(24.dp)) }
        item {
            Text("Revisión ITV", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            SummaryBanner(uiState)
        }
        items(uiState.monitors) { monitor ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(monitor.nameEs, fontWeight = FontWeight.SemiBold)
                Text(statusLabel(monitor.status), color = statusColor(monitor.status), fontWeight = FontWeight.Bold)
            }
        }
        item {
            Text(
                "En España, la ITV acepta hasta 2 monitores incompletos",
                color = Color(0xFF667085),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun SummaryBanner(uiState: ItvUiState) {
    val text = if (uiState.readyForItv) {
        "Tu coche está listo para la ITV"
    } else {
        "Faltan ${uiState.incompleteCount} pruebas por completar — conduce 50–100 km en carretera y vuelve a comprobar"
    }
    val color = if (uiState.readyForItv) Color(0xFFE7F8EE) else Color(0xFFFFF3D6)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(text, modifier = Modifier.padding(18.dp), fontWeight = FontWeight.Bold)
    }
}

private fun statusLabel(status: ItvMonitorStatus): String = when (status) {
    ItvMonitorStatus.LISTO -> "✅ Listo"
    ItvMonitorStatus.EN_PROCESO -> "⏳ En proceso"
    ItvMonitorStatus.NO_LISTO -> "❌ No listo"
}

private fun statusColor(status: ItvMonitorStatus): Color = when (status) {
    ItvMonitorStatus.LISTO -> Color(0xFF16A34A)
    ItvMonitorStatus.EN_PROCESO -> Color(0xFFD97706)
    ItvMonitorStatus.NO_LISTO -> Color(0xFFDC2626)
}
