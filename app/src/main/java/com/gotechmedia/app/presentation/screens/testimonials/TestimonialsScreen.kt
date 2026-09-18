package com.gotechmedia.app.presentation.screens.testimonials

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

/**
 * Elegant empty state for Client Testimonials.
 * Complies strictly with zero fake data and NDA confidentiality standards.
 */
@Composable
fun TestimonialsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Testimonials",
                subtitle = "Client Endorsements & Privacy",
                onBackClick = onNavigateBack
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("testimonials_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("testimonials_empty_card")
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Confidentiality Protection",
                            tint = ElectricCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Client Privacy & Confidentiality",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, GlassBorderSubtle, CircleShape)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STRICT NDA GOVERNANCE",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "To safeguard proprietary innovations and competitive advantage, our enterprise and venture client engagements are governed by non-disclosure agreements. We do not publish fabricated endorsements or vanity logos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Verified references, blind architectural case studies, and engineering peer audits can be furnished directly to prospective partners upon request during the consultation phase.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryCtaButton(
                        text = "Request Verified References",
                        onClick = onNavigateToContact,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "testimonials_request_references_btn"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Or explore custom project scoping",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onNavigateToQuote("") }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
