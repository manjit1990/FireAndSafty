package com.learningapp.firesafetyservicemanagement

import androidx.compose.ui.window.ComposeUIViewController
import com.yoga.firesafety.shared.presentation.MainApp
import com.yoga.firesafety.shared.presentation.theme.FireSafetyTheme

fun MainViewController() = ComposeUIViewController {
    FireSafetyTheme {
        MainApp()
    }
}
