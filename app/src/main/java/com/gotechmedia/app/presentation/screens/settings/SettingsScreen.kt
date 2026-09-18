package com.gotechmedia.app.presentation.screens.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hapticsEnabled by remember { mutableStateOf(true) }
    var highPerfAnimations by remember { mutableStateOf(true) }
    var sprintNotifications by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Settings",
                subtitle = "App Preferences & Compliance",
                onBackClick = onNavigateBack
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("settings_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Appearance & System Performance
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "INTERFACE & PERFORMANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Dark Mode Row
                            SettingRowWithText(
                                icon = Icons.Outlined.DarkMode,
                                title = "Appearance Mode",
                                subtitle = "Dark Mode Only (Mandated by Brand Identity)",
                                trailing = {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(ElectricCyan.copy(alpha = 0.12f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Obsidian",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricCyan
                                        )
                                    }
                                }
                            )

                            // Haptic Feedback
                            SettingRowWithText(
                                icon = Icons.Outlined.Vibration,
                                title = "Tactile Micro-Interactions",
                                subtitle = "Subtle tactile response on primary CTA gestures",
                                trailing = {
                                    Switch(
                                        checked = hapticsEnabled,
                                        onCheckedChange = { hapticsEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ObsidianCanvas,
                                            checkedTrackColor = ElectricCyan,
                                            uncheckedThumbColor = TextMuted,
                                            uncheckedTrackColor = ObsidianSurface
                                        )
                                    )
                                }
                            )

                            // Smooth 60/120fps Animations
                            SettingRowWithText(
                                icon = Icons.Outlined.Speed,
                                title = "Hardware Accelerated Transitions",
                                subtitle = "Full-frame rate easing curves and subtle depth",
                                trailing = {
                                    Switch(
                                        checked = highPerfAnimations,
                                        onCheckedChange = { highPerfAnimations = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ObsidianCanvas,
                                            checkedTrackColor = ElectricCyan,
                                            uncheckedThumbColor = TextMuted,
                                            uncheckedTrackColor = ObsidianSurface
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Client Notifications
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ENGAGEMENT ALERTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SettingRowWithText(
                                icon = Icons.Outlined.Notifications,
                                title = "Milestone & Sprint Alerts",
                                subtitle = "Receive instant notifications upon deliverable approval",
                                trailing = {
                                    Switch(
                                        checked = sprintNotifications,
                                        onCheckedChange = { sprintNotifications = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ObsidianCanvas,
                                            checkedTrackColor = ElectricCyan,
                                            uncheckedThumbColor = TextMuted,
                                            uncheckedTrackColor = ObsidianSurface
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Legal & Compliance
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "LEGAL & COMPLIANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Mutual Confidentiality Agreement (NDA)",
                                fontSize = 13.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                    .padding(vertical = 4.dp)
                            )
                            Text(
                                text = "Software Intellectual Property Ownership Terms",
                                fontSize = 13.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                    .padding(vertical = 4.dp)
                            )
                            Text(
                                text = "Privacy & Data Protection Notice",
                                fontSize = 13.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // App Engine Build Metadata
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GoTech Media Client Application",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Architecture Engine v1.0.0 • Production Build",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Engineered with Kotlin & Jetpack Compose",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRowWithText(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        trailing()
    }
}
