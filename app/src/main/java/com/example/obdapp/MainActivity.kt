package com.example.obdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.obdapp.ui.navigation.AppNavGraph
import com.example.obdapp.ui.theme.ObdAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ObdAppTheme {
                AppNavGraph(languageSelected = false)
            }
        }
    }
}
