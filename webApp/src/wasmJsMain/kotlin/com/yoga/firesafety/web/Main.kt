package com.yoga.firesafety.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.yoga.firesafety.shared.config.initKoin
import com.yoga.firesafety.shared.data.local.DriverFactory
import com.yoga.firesafety.shared.presentation.MainApp
import com.yoga.firesafety.shared.presentation.theme.FireSafetyTheme
import kotlinx.browser.document
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin {
        modules(module {
            single { DriverFactory() }
        })
    }
    
    ComposeViewport(document.body!!) {
        FireSafetyTheme {
            MainApp()
        }
    }
}
