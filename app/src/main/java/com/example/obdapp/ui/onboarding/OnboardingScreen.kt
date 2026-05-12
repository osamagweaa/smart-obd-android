package com.example.obdapp.ui.onboarding

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.obdapp.R

@Composable
fun OnboardingScreen(onStart: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.height(48.dp))
        CarLottieAnimation()
        Text("AutoDiag", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16202A))
        Text(
            "Convierte un adaptador Bluetooth ELM327 barato en un asesor claro para la luz del motor, la ITV y el DPF.",
            textAlign = TextAlign.Center,
            color = Color(0xFF667085)
        )
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Qué necesitas", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("1. Un adaptador ELM327 Bluetooth enchufado al coche.")
                Text("2. Emparejarlo desde ajustes de Android.")
                Text("3. Abrir AutoDiag y tocar conectar.")
            }
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.amazon.es/s?k=ELM327+Bluetooth+OBD2")
                )
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar ELM327 en Amazon.es")
        }
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text("Empezar")
        }
    }
}

@Composable
fun CarLottieAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.repair_car))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.height(170.dp)
    )
}
