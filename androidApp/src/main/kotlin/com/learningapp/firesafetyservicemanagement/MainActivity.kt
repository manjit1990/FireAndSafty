package com.learningapp.firesafetyservicemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yoga.firesafety.shared.presentation.MainApp
import com.yoga.firesafety.shared.presentation.theme.FireSafetyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            FireSafetyTheme {
                MainApp()
            }
        }
    }
}
