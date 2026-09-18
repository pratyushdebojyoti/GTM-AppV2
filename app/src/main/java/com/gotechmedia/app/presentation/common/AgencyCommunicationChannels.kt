package com.gotechmedia.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.core.utils.CommunicationIntentHandler
import com.gotechmedia.app.core.utils.Constants
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.GlassSurface
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

/**
 * Universal Agency Communication Channels Grid.
 * Provides the 4 required direct channels:
 * - WhatsApp button (Deep link with prefilled text to +919476325586)
 * - Call button (Dialer intent to +919476325586)
 * - Email button (Email chooser intent to inquiries@gotechmedia.com)
 * - Website button (External browser intent to https://gotechmedia.com)
 */
@Composable
fun AgencyCommunicationChannels(
    modifier: Modifier = Modifier,
    sourceScreen: String = "contact",
    onActionCompleted: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val analytics = container.analyticsHelper

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // WhatsApp High-Priority Button
        CommunicationChannelCard(
            title = "Chat on WhatsApp",
            subtitle = "+91 94763 25586 • Instant Chat",
            badge = "DIRECT LINE",
            icon = Icons.AutoMirrored.Outlined.Chat,
            accentColor = Color(0xFF25D366),
            testTag = "channel_whatsapp_button",
            onClick = {
                CommunicationIntentHandler.openWhatsApp(
                    context = context,
                    phoneNumberDigits = Constants.AGENCY_WHATSAPP_PHONE_DIGITS,
                    message = Constants.WHATSAPP_PREFILLED_MESSAGE,
                    analyticsHelper = analytics,
                    sourceScreen = sourceScreen
                )
                onActionCompleted?.invoke()
            }
        )

        // 3-Way Grid for Call, Email, and Website
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Call Button
            CommunicationMiniCard(
                title = "Call",
                detail = "+91 94763...",
                icon = Icons.Outlined.Call,
                accentColor = ElectricCyan,
                modifier = Modifier.weight(1f),
                testTag = "channel_call_button",
                onClick = {
                    CommunicationIntentHandler.dialPhoneNumber(
                        context = context,
                        phoneNumber = Constants.AGENCY_CALL_NUMBER,
                        analyticsHelper = analytics,
                        sourceScreen = sourceScreen
                    )
                    onActionCompleted?.invoke()
                }
            )

            // Email Button
            CommunicationMiniCard(
                title = "Email",
                detail = "Official SLA",
                icon = Icons.Outlined.Email,
                accentColor = Color(0xFF6C63FF),
                modifier = Modifier.weight(1f),
                testTag = "channel_email_button",
                onClick = {
                    CommunicationIntentHandler.sendEmail(
                        context = context,
                        recipientEmail = Constants.AGENCY_EMAIL,
                        analyticsHelper = analytics,
                        sourceScreen = sourceScreen
                    )
                    onActionCompleted?.invoke()
                }
            )

            // Website Button
            CommunicationMiniCard(
                title = "Website",
                detail = "gotechmedia.com",
                icon = Icons.Outlined.Language,
                accentColor = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f),
                testTag = "channel_website_button",
                onClick = {
                    CommunicationIntentHandler.openWebsite(
                        context = context,
                        url = Constants.AGENCY_WEBSITE_URL,
                        analyticsHelper = analytics,
                        sourceScreen = sourceScreen
                    )
                    onActionCompleted?.invoke()
                }
            )
        }
    }
}

@Composable
private fun CommunicationChannelCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassSurface)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun CommunicationMiniCard(
    title: String,
    detail: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianSurface)
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = detail,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}
