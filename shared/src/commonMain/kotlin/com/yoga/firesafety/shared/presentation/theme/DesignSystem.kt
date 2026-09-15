package com.yoga.firesafety.shared.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppColors {
    // Premium Light Palette
    val Primary = Color(0xFF1E3A8A) // Refined Navy
    val Secondary = Color(0xFF10B981) // Modern Emerald
    val Background = Color(0xFFF8FAFC)
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF0F172A)
    val OnSurface = Color(0xFF0F172A)
    val OnSurfaceVariant = Color(0xFF64748B)
    val Outline = Color(0xFFCBD5E1)

    // Next-Gen Dark Palette (Deep Space)
    val DarkPrimary = Color(0xFF00D2FF) // Electric Cyan
    val DarkSecondary = Color(0xFF34D399) // Emerald
    val DarkBackground = Color(0xFF0A0F1D) // Deep Space Navy
    val DarkSurface = Color(0xFF161B2C) // Muted Surface
    val DarkOnPrimary = Color(0xFF0A0F1D)
    val DarkOnSecondary = Color(0xFF0A0F1D)
    val DarkOnBackground = Color(0xFFF8FAFC)
    val DarkOnSurface = Color(0xFFF8FAFC)
    val DarkOnSurfaceVariant = Color(0xFF94A3B8)
    val DarkOutline = Color(0xFF23293F)
    
    val Error = Color(0xFFFF4B66)
    val Success = Color(0xFF00D2FF)
}

object AppGradients {
    val DeepSpace = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(Color(0xFF0A0F1D), Color(0xFF020409))
    )
    val Midnight = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(Color(0xFF0A0F1D), Color(0xFF020409))
    )
    val CyanGlow = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(Color(0xFF00D2FF), Color(0xFF3B82F6))
    )
    val GlassEffect = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
    )
}

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)
