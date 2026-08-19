package com.squillaci.autodiag.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.widthIn
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.squillaci.autodiag.domain.model.ObdConnectionState
import com.squillaci.autodiag.ui.navigation.Route

private val AutoDiagBg = Color(0xFFF5F7FA)
private val CardWhite = Color.White
private val PrimaryText = Color(0xFF16202A)
private val SecondaryText = Color(0xFF667085)
private val Blue = Color(0xFF2563EB)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AutoDiagBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(24.dp))
            ConnectionChip(uiState.connectionState) {
                navController.navigate(Route.Bluetooth.name)
            }
        }
        item {
            Text("AutoDiag", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text(uiState.vehicleInfo.displayName, fontSize = 18.sp, color = SecondaryText)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    HomeCard("🔴", "Luz del motor", "Entiende la avería sin tecnicismos", Modifier.weight(1f)) {
                        navController.navigate(Route.Dtc.name)
                    }
                    HomeCard("✅", "¿Puedo conducir?", "Consejo rápido para hoy", Modifier.weight(1f)) {
                        navController.navigate(Route.SafeToday.name)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    HomeCard("📋", "ITV", "Comprueba monitores OBD", Modifier.weight(1f)) {
                        navController.navigate(Route.Itv.name)
                    }
                    HomeCard("💨", "DPF", "Estado del filtro diésel", Modifier.weight(1f)) {
                        navController.navigate(Route.Dpf.name)
                    }
                }
                HomeCard("📄", "Informe", "Texto listo para WhatsApp o email", Modifier.fillMaxWidth()) {
                    navController.navigate(Route.Report.name)
                }
            }
        }
        item {
            LiveStrip(uiState)
        }
    }
}

@Composable
private fun ConnectionChip(state: ObdConnectionState, onClick: () -> Unit) {
    val label = when (state) {
        is ObdConnectionState.Connected -> "🔵 Conectado — ${state.deviceName}"
        is ObdConnectionState.Connecting -> "🔵 Conectando..."
        is ObdConnectionState.Error -> "⚪ ${state.messageEs}"
        else -> "⚪ Sin conexión — Toca para conectar"
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = if (state is ObdConnectionState.Connected) Color(0xFFE8F2FF) else Color.White,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), color = PrimaryText)
    }
}

@Composable
private fun HomeCard(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(icon, fontSize = 26.sp)
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp)
                Text(subtitle, color = SecondaryText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun LiveStrip(uiState: HomeUiState) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Datos en vivo", fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(10.dp))
            if (uiState.stripPids.isEmpty()) {
                Text("Conecta un ELM327 para ver RPM, temperatura y velocidad.", color = SecondaryText)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.stripPids.forEach { pid ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(14.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text("${pid.labelEs}: ${pid.displayValue} ${pid.unit}", color = Blue, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
