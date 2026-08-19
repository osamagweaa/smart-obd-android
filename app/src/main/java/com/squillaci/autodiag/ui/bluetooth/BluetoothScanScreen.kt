package com.squillaci.autodiag.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.squillaci.autodiag.domain.model.ObdConnectionState


@Composable
@SuppressLint("MissingPermission")
fun BluetoothScanScreen(
    devices: List<BluetoothDevice>,
    viewModel: BluetoothConnectViewModel,
    navController: NavController,
    onSkip: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val connectionState = uiState.connectionState

    val AccentBlue = Color(0xFF007AFF)
    val AppBg = Color(0xFFF5F5F5)
    val textPrimary = Color(0xFF1A1A1A)
    val textSecondary = Color(0xFF555555)

    LaunchedEffect(connectionState) {
        if (connectionState is ObdConnectionState.Connected) {
            navController.navigate("home") {
                popUpTo("bluetooth") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(16.dp)
            //.padding(horizontal = 20.dp, vertical = 32.dp),
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(devices) { device ->
                DeviceItem(
                    device = device,
                    onClick = { viewModel.connect(device.name ?: device.address) },
                    AccentBlue = AccentBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    AppBg = AppBg
                )
            }
        }

        when (connectionState) {
            ObdConnectionState.Connecting -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                    Spacer(Modifier.height(8.dp))
                    Text("Conectando...", color = textPrimary)
                }
            }
            is ObdConnectionState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Adaptador no encontrado. Asegúrate de que el ELM327 está enchufado y emparejado por Bluetooth.", color = textPrimary)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.retry() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Reintentar", color = Color.White)
                    }
                }
            }
            else -> {}
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Continuar sin conectar", color = textPrimary)
        }
    }
}

@Composable
@SuppressLint("MissingPermission")
fun DeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit,
    AccentBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    AppBg: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = null,
                tint = AccentBlue
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name ?: "Adaptador sin nombre",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary
                )
            }

            Button(
                onClick = { onClick.invoke() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Conectar", color = Color.White)
            }
        }
    }
}
@SuppressLint("MissingPermission")
fun startBluetoothScan(
    context: Context,
    onDeviceFound: (BluetoothDevice) -> Unit
) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.takeIf(::isElmDevice)?.let { onDeviceFound(it) }
            }
        }
    }

    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(receiver, filter)

    bluetoothAdapter.startDiscovery()
}

@SuppressLint("MissingPermission")
fun isElmDevice(device: BluetoothDevice): Boolean {
    val name = device.name.orEmpty().uppercase()
    return listOf("OBD", "ELM", "OBDII", "TORQUE", "VGATE", "KONNWEI").any { it in name }
}