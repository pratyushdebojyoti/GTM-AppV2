package com.gotechmedia.app.presentation.screens.servicedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.presentation.base.UiState
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.EmptyStateView
import com.gotechmedia.app.presentation.common.ErrorStateView
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.LoadingStateView
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceDetailsScreen(
    viewModel: ServiceDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Service Details",
                subtitle = "Practice Specifications",
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            if (state is UiState.Success) {
                val service = (state as UiState.Success).data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianCanvas.copy(alpha = 0.95f))
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(20.dp)
                ) {
                    PrimaryCtaButton(
                        text = "Get a Custom Quote for ${service.title}",
                        onClick = { onNavigateToQuote(service.id) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "service_details_quote_cta"
                    )
                }
            }
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("service_details_screen")
    ) { innerPadding ->
        when (val uiState = state) {
            is UiState.Loading -> {
                LoadingStateView(
                    message = "Loading service specifications...",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Empty -> {
                EmptyStateView(
                    title = "Service Not Found",
                    description = "The requested practice discipline could not be identified.",
                    actionText = "Back to Services",
                    onActionClick = onNavigateBack,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Error -> {
                ErrorStateView(
                    message = uiState.message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Success -> {
                val service = uiState.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Card
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            TechBadge(text = service.category)

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = service.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // Pricing Policy Disclaimer (No fixed pricing)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Custom Scoping: We do not offer fixed off-the-shelf pricing. Every quote is custom-calculated around your exact architecture requirements and milestones.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // Capabilities Checklist
                    if (service.capabilities.isNotEmpty()) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "CORE CAPABILITIES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    service.capabilities.forEach { capability ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricCyan.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = ElectricCyan,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = capability,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Deliverables
                    if (service.deliverables.isNotEmpty()) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "KEY DELIVERABLES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    service.deliverables.forEach { deliverable ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricCyan)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = deliverable,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Methodology
                    if (service.methodology.isNotBlank()) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "DELIVERY METHODOLOGY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = service.methodology,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    // Recommended Tech Stack
                    if (service.techStack.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "TECHNOLOGY STACK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    service.techStack.forEach { tech ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ObsidianSurface)
                                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = tech,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
