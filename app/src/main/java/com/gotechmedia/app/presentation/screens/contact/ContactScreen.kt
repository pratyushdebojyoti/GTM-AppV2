package com.gotechmedia.app.presentation.screens.contact

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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val subject by viewModel.subject.collectAsState()
    val message by viewModel.message.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Contact",
                subtitle = "Direct Communication Channels",
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                            .clickable { onNavigateToQuote("") }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("contact_quote_cta")
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
        modifier = modifier.testTag("contact_screen")
    ) { innerPadding ->
        if (submissionState is ContactSubmissionState.Success) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Success",
                            tint = ElectricCyan,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Message Dispatched",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Your transmission has been forwarded to our senior client relations and engineering teams. We will respond within 24 business hours.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryCtaButton(
                            text = "Send Another Message",
                            onClick = { viewModel.reset() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Info Banner
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Direct Inquiry & Consultation",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Have a question about technical feasibility, engineering partnerships, or practice capabilities? Our senior team is at your disposal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Interactive Communication Channels (WhatsApp, Call, Email, Website)
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "INSTANT COMMUNICATION CHANNELS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        com.gotechmedia.app.presentation.common.AgencyCommunicationChannels(
                            sourceScreen = "contact"
                        )
                    }
                }

                // Agency Channels
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ObsidianSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Email,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Official Inquiries",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "inquiries@gotechmedia.com",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ObsidianSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Schedule,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Turnaround SLA",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "< 24 Hours Guaranteed Response",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ObsidianSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Locations",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "San Francisco • London • Singapore",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Message if any
                if (submissionState is ContactSubmissionState.Error) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = (submissionState as ContactSubmissionState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Direct Contact Form
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "SEND A DIRECT MESSAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.setName(it) },
                            label = { Text("Your Name *") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.setEmail(it) },
                            label = { Text("Work Email *") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = subject,
                            onValueChange = { viewModel.setSubject(it) },
                            label = { Text("Subject") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_subject_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { viewModel.setMessage(it) },
                            label = { Text("Message *") },
                            minLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_message_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryCtaButton(
                            text = "Dispatch Message",
                            onClick = { viewModel.submit() },
                            isLoading = submissionState is ContactSubmissionState.Submitting,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "contact_submit_button"
                        )
                    }
                }
            }
        }
    }
}
