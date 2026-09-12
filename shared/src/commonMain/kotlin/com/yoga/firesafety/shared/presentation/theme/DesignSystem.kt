package com.yoga.firesafety.shared.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppColors {
    // Light Palette
    val Primary = Color(0xFF0D47A1)
    val Secondary = Color(0xFF2E7D32)
    val Background = Color(0xFFF5F7FA)
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF1A1C1E)
    val OnSurface = Color(0xFF1A1C1E)

    // Dark Palette (Deep Midnight)
    val DarkPrimary = Color(0xFFD0E4FF)
    val DarkSecondary = Color(0xFF81C784)
    val DarkBackground = Color(0xFF0F111A)
    val DarkSurface = Color(0xFF1B1E2B)
    val DarkOnPrimary = Color(0xFF003258)
    val DarkOnBackground = Color(0xFFE2E2E6)
    val DarkOnSurface = Color(0xFFE2E2E6)
    
    val Error = Color(0xFFB00020)
    val Outline = Color(0xFF74777F)
}

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    )
)
