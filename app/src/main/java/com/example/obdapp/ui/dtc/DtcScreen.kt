package com.example.obdapp.ui.dtc

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.obdapp.domain.model.DtcInfo
import com.example.obdapp.domain.model.DtcSeverity

@Composable
fun DtcScreen(
    onOpenReport: () -> Unit,
    viewModel: DtcViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var confirmClear by remember { mutableStateOf(false) }
    val hasFaults = uiState.result.milOn || uiState.result.allCodes.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(22.dp)) }
        item {
            Text("Luz del motor", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            StatusBadge(hasFaults)
        }
        items(uiState.result.allCodes) { dtc ->
            DtcCard(dtc)
        }
        if (!hasFaults) {
            item {
                Text("No hay averías activas guardadas en este momento.", color = Color(0xFF667085))
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { confirmClear = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = hasFaults
                ) { Text("Borrar averías") }
                OutlinedButton(onClick = onOpenReport, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver informe mecánico")
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("¿Borrar averías?") },
            text = { Text("Esto apaga los avisos guardados, pero no repara la causa. Hazlo solo después de revisar el problema.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    viewModel.clear()
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun StatusBadge(hasFaults: Boolean) {
    val color = if (hasFaults) Color(0xFFFFF3D6) else Color(0xFFE7F8EE)
    val text = if (hasFaults) "⚠️ Avería detectada" else "✅ Sin averías"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(18.dp))
    }
}

@Composable
private fun DtcCard(dtc: DtcInfo) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(dtc.titleEs, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                SeverityBadge(dtc.severity)
            }
            Text(dtc.descriptionEs, color = Color(0xFF475467))
            Text(
                "Coste orientativo: ${dtc.estimatedRepairEuros.first}-${dtc.estimatedRepairEuros.last}€",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SeverityBadge(severity: DtcSeverity) {
    val color = when (severity) {
        DtcSeverity.GRAVE -> Color(0xFFDC2626)
        DtcSeverity.MODERADO -> Color(0xFFD97706)
        DtcSeverity.INFORMATIVO -> Color(0xFF2563EB)
    }
    Text(
        severity.name,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    )
}
