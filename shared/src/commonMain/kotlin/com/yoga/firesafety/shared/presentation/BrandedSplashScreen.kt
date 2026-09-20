package com.yoga.firesafety.shared.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import firesafetyservicemanagement.shared.generated.resources.Res
import firesafetyservicemanagement.shared.generated.resources.app_logo

@Composable
fun BrandedSplashScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A13)) // Deep Navy/Black Background
    ) {
        // Main Content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Logo
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(32.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // A Safety Platform Badge
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(100.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4B66).copy(alpha = 0.5f))
            ) {
                Text(
                    text = "A SAFETY PLATFORM",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TORBRAM Title
            Text(
                text = "TORBRAM",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 42.sp,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // FIRE PROTECTION
            Text(
                text = "FIRE PROTECTION",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFF4B66), // High-contrast Red
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Service Subtitle
            Text(
                text = "INSPECTION  •  INSTALLATION  •  DISPATCH",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Loading Status
            Text(
                text = "CONNECTING SECURE DISPATCH...",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Progress Bar
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(Color(0xFFFF4B66), RoundedCornerShape(2.dp))
                )
            }
        }

        // Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "TORBRAM SAFETY SUITE  •  v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.2f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(0.dp),
    color: Color = Color.White,
    border: androidx.compose.foundation.BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color, shape)
            .then(if (border != null) Modifier.border(border, shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
