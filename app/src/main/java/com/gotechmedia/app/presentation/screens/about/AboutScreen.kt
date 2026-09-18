package com.gotechmedia.app.presentation.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.GoTechLogo
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

data class AgencyPillar(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onNavigateToContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillars = listOf(
        AgencyPillar(
            title = "Digital Sovereignty",
            description = "We believe enterprises must own their technology. We write clean, unencumbered, fully documented source code with zero proprietary lock-in. You own 100% of your codebase, repositories, and cloud assets.",
            icon = Icons.Outlined.Shield
        ),
        AgencyPillar(
            title = "Apple-Inspired Craft",
            description = "Every tactile touch interaction, transition curve, and typography scale is deliberate. We design modern interfaces that feel restrained, responsive, and unmistakably premium.",
            icon = Icons.Outlined.Terminal
        ),
        AgencyPillar(
            title = "Full-Funnel Synergy",
            description = "Software engineering and commercial growth cannot exist in silos. We bridge high-performance native code with technical SEO, attribution pipelines, and customer acquisition channels.",
            icon = Icons.Outlined.TrendingUp
        ),
        AgencyPillar(
            title = "Global Engineering Standards",
            description = "Operating with distributed engineering pods across San Francisco, London, and Singapore to deliver continuous collaboration, sub-24h turnaround, and rigorous code reviews.",
            icon = Icons.Outlined.Public
        )
    )

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "About Us",
                subtitle = "Philosophy & Agency Standards",
                onBackClick = onNavigateBack,
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                            .clickable { onNavigateToQuote("") }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Quote",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("about_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Identity Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GoTechLogo(
                        symbolSize = 42.dp,
                        titleSize = 28.dp,
                        subtitleSize = 18.dp,
                        showGlow = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Engineering the Future of Digital Ventures",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GoTech Media is a modern technology and digital media agency. Founded to eliminate the friction between engineering precision and creative marketing, we build software systems and growth frameworks that market leaders rely upon.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Core Pillars
            item {
                Text(
                    text = "CORE PRINCIPLES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            items(pillars) { pillar ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ObsidianSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = pillar.icon,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = pillar.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = pillar.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Operating Locations
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "GLOBAL PRESENCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "San Francisco • London • Singapore",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Synchronized engineering time zones ensuring continuous progress and real-time executive consultation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // CTA Box
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    PrimaryCtaButton(
                        text = "Get a Custom Quote",
                        onClick = { onNavigateToQuote("") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Need immediate consultation? Contact us",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { onNavigateToContact() }
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}
