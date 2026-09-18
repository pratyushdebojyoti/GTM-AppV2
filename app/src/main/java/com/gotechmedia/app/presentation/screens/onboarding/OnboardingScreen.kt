package com.gotechmedia.app.presentation.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.presentation.common.GoTechSymbol
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val badgeText: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Engineering Digital Sovereignty",
                subtitle = "Bespoke software architecture without compromises",
                description = "We engineer native Android applications, complex web portals, and scalable cloud systems. Every line of code is custom-crafted to give your venture an enduring technical advantage.",
                icon = Icons.Outlined.Layers,
                badgeText = "Clean Architecture"
            ),
            OnboardingStep(
                title = "14 Full-Stack Practice Disciplines",
                subtitle = "End-to-end capabilities under one roof",
                description = "From UI/UX systems and high-converting ad architectures to custom generative AI integrations and workflow automation, we eliminate agency fragmentation.",
                icon = Icons.Outlined.AutoAwesome,
                badgeText = "Complete Lifecycle"
            ),
            OnboardingStep(
                title = "Direct Senior Craft & Strategy",
                subtitle = "Transparent milestones and tangible ROI",
                description = "Zero junior handoffs, no template recycling, and no opaque billing. Receive direct senior architectural guidance, rapid milestone deployments, and comprehensive post-launch support.",
                icon = Icons.Outlined.RocketLaunch,
                badgeText = "Enterprise Standards"
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianCanvas)
            .statusBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header with Logo & Skip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoTechSymbol(size = 36.dp)

            Text(
                text = "Skip",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onFinishOnboarding() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("onboarding_skip_button")
            )
        }

        // Slide Content
        AnimatedContent(
            targetState = steps[currentStep],
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboarding_slide"
        ) { step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Feature Icon Badge
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ObsidianSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ElectricCyan.copy(alpha = 0.12f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = step.badgeText,
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = step.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ElectricCyan.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pagination Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.indices.forEach { index ->
                    val isSelected = index == currentStep
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(6.dp)
                            .width(if (isSelected) 24.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ElectricCyan else Color.White.copy(alpha = 0.2f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryCtaButton(
                text = if (currentStep == steps.lastIndex) "Get Started" else "Next Step",
                onClick = {
                    if (currentStep < steps.lastIndex) {
                        currentStep++
                    } else {
                        onFinishOnboarding()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "onboarding_next_button"
            )
        }
    }
}
