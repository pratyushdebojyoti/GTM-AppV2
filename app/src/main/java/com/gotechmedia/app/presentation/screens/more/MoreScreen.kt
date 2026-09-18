package com.gotechmedia.app.presentation.screens.more

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
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
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun MoreScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateToProcess: () -> Unit,
    onNavigateToTestimonials: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "More",
                subtitle = "Agency Hub & Preferences",
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                            .clickable { onNavigateToQuote("") }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("more_quote_cta")
                    ) {
                        Text(
                            text = "Get a Quote",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("more_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Brand Logo Header
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    GoTechLogo(
                        symbolSize = 38.dp,
                        titleSize = 24.dp,
                        subtitleSize = 16.dp,
                        showGlow = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Engineering Digital Sovereignty • Modern Technology Agency",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Primary CTA Banner
            item {
                PrimaryCtaButton(
                    text = "Get a Custom Quote",
                    onClick = { onNavigateToQuote("") },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "more_primary_quote_btn"
                )
            }

            // Agency & Engineering Section
            item {
                SectionHeader(title = "AGENCY & ARCHITECTURE")
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        MoreMenuItem(
                            icon = Icons.Outlined.Info,
                            title = "About GoTech Media",
                            subtitle = "Agency vision, principles, and engineering sovereignty",
                            onClick = onNavigateToAbout
                        )
                        MoreDivider()
                        MoreMenuItem(
                            icon = Icons.Outlined.Engineering,
                            title = "Agency Delivery Process",
                            subtitle = "The 6-stage delivery lifecycle from Discovery to Scale",
                            onClick = onNavigateToProcess
                        )
                        MoreDivider()
                        MoreMenuItem(
                            icon = Icons.Outlined.Lock,
                            title = "Client Privacy & Testimonials",
                            subtitle = "Enterprise NDA governance & verified references",
                            onClick = onNavigateToTestimonials
                        )
                    }
                }
            }

            // Client Workspace & Portal Section
            item {
                SectionHeader(title = "CLIENT WORKSPACE")
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        MoreMenuItem(
                            icon = Icons.Outlined.AccountCircle,
                            title = "Client Dashboard",
                            subtitle = "Active projects, sprint progress, milestones & tickets",
                            onClick = onNavigateToProfile
                        )
                        MoreDivider()
                        MoreMenuItem(
                            icon = Icons.Outlined.Login,
                            title = "Client Portal Sign In",
                            subtitle = "Access authorized client deliverables and sprint reviews",
                            onClick = onNavigateToLogin
                        )
                        MoreDivider()
                        MoreMenuItem(
                            icon = Icons.Outlined.PersonAdd,
                            title = "Create Partner Account",
                            subtitle = "Register as a prospective enterprise partner",
                            onClick = onNavigateToRegister
                        )
                    }
                }
            }

            // App Settings & Preferences
            item {
                SectionHeader(title = "PREFERENCES & COMPLIANCE")
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        MoreMenuItem(
                            icon = Icons.Outlined.Settings,
                            title = "Settings",
                            subtitle = "Haptics, animations, alerts, and legal compliance",
                            onClick = onNavigateToSettings
                        )
                    }
                }
            }

            // Direct Communication Channels
            item {
                SectionHeader(title = "DIRECT AGENCY HOTLINES")
                Spacer(modifier = Modifier.height(8.dp))
                com.gotechmedia.app.presentation.common.AgencyCommunicationChannels(
                    sourceScreen = "more"
                )
            }

            // Footer info
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GoTech Media • San Francisco | London | Singapore",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Confidential Scoping & Zero Proprietary Lock-In",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = ElectricCyan,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp
    )
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun MoreDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.05f))
    )
}
