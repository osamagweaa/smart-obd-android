package com.squillaci.autodiag.ui.report

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.widthIn
import androidx.hilt.navigation.compose.hiltViewModel
import com.squillaci.autodiag.ui.AdaptiveContent

@Composable
fun ReportScreen(viewModel: ReportViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = if (isTablet) 960.dp else Dp.Infinity)
            .background(Color(0xFFF5F7FA))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Informe para el mecánico", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                uiState.reportText,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF344054)
            )
        }
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Informe AutoDiag")
                    putExtra(Intent.EXTRA_TEXT, uiState.reportText)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir informe"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir informe")
        }
    }
}
