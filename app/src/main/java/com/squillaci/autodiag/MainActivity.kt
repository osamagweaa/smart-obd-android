package com.squillaci.autodiag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.squillaci.autodiag.ui.navigation.AppNavGraph
import com.squillaci.autodiag.ui.theme.ObdAppTheme
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
