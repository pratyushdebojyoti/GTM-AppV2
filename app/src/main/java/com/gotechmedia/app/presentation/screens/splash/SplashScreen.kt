package com.gotechmedia.app.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.presentation.common.GoTechLogo
import com.gotechmedia.app.presentation.common.GoTechSymbol
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Splash screen featuring the official GoTech Media mark with subtle ambient glow
 * and smooth Apple-style entrance transition.
 */
@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit,
    onSkipToHome: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0.2f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        glowAlpha.animateTo(
            targetValue = 0.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(2200)
        onNavigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianCanvas)
            .statusBarsPadding()
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Radial Glow
        Box(
            modifier = Modifier
                .size(340.dp)
                .alpha(glowAlpha.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(horizontal = 32.dp)
        ) {
            // Official GoTech Media Brand Symbol
            GoTechSymbol(
                size = 96.dp,
                showGlow = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Full GoTech Media Logo
            GoTechLogo(
                symbolSize = 0.dp,
                titleSize = 34.dp,
                subtitleSize = 20.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Engineering Digital Sovereignty & Next-Gen Software",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = ElectricCyan,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }

        // Tap to skip direct link
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSkipToHome() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("splash_skip_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore Agency",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
