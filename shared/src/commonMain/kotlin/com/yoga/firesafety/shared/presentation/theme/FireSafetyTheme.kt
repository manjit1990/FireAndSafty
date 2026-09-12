package com.yoga.firesafety.shared.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    background = AppColors.Background,
    surface = AppColors.Surface,
    onPrimary = AppColors.OnPrimary,
    onBackground = AppColors.OnBackground,
    onSurface = AppColors.OnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.DarkPrimary,
    secondary = AppColors.DarkSecondary,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    onPrimary = AppColors.DarkOnPrimary,
    onBackground = AppColors.DarkOnBackground,
    onSurface = AppColors.DarkOnSurface
)

@Composable
fun FireSafetyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
