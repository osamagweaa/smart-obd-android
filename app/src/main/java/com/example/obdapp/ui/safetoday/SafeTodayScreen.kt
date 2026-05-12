package com.example.obdapp.ui.safetoday

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
import com.example.obdapp.domain.model.SafetyVerdictLevel

@Composable
fun SafeTodayScreen(viewModel: SafeTodayViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val color = when (uiState.verdict.level) {
        SafetyVerdictLevel.GREEN -> Color(0xFF16A34A)
        SafetyVerdictLevel.AMBER -> Color(0xFFD97706)
        SafetyVerdictLevel.RED -> Color(0xFFDC2626)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("¿Puedo conducir hoy?", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(uiState.verdict.titleEs, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(uiState.verdict.explanationEs, color = Color.White)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            uiState.verdict.criticalPids.forEach { pid ->
                Text(
                    "${pid.labelEs}: ${pid.displayValue} ${pid.unit}",
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    color = Color(0xFF344054),
                    fontSize = 12.sp
                )
            }
        }
    }
}
