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
    error = AppColors.Error,
    onPrimary = AppColors.OnPrimary,
    onSecondary = AppColors.OnSecondary,
    onBackground = AppColors.OnBackground,
    onSurface = AppColors.OnSurface,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    outline = AppColors.Outline
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.DarkPrimary,
    secondary = AppColors.DarkSecondary,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    error = AppColors.Error,
    onPrimary = AppColors.DarkOnPrimary,
    onSecondary = AppColors.DarkOnSecondary,
    onBackground = AppColors.DarkOnBackground,
    onSurface = AppColors.DarkOnSurface,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant,
    outline = AppColors.DarkOutline
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
