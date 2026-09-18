package com.gotechmedia.app.presentation.screens.process

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.gotechmedia.app.domain.model.AgencyProcessStep
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

@Composable
fun AgencyProcessScreen(
    viewModel: AgencyProcessViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Agency Process",
                subtitle = "Engineering Delivery Lifecycle",
                onBackClick = onNavigateBack
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("agency_process_screen")
    ) { innerPadding ->
        when (val uiState = state) {
            is UiState.Loading -> {
                LoadingStateView(
                    message = "Loading engineering lifecycle...",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Empty -> {
                EmptyStateView(
                    title = "Process Steps Unavailable",
                    description = "Engineering lifecycle documentation is being updated.",
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
                val stepList = uiState.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "How GoTech Media Delivers Software",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Our structured 6-phase delivery model ensures predictable timelines, pristine code quality, and continuous executive alignment from initial discovery to scaled production.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    items(stepList, key = { it.stepNumber }) { step ->
                        ProcessStepItem(step = step)
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            PrimaryCtaButton(
                                text = "Initiate Phase 1 Discovery",
                                onClick = { onNavigateToQuote("") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcessStepItem(
    step: AgencyProcessStep
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("process_step_${step.stepNumber}")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                TechBadge(text = step.duration)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = step.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ElectricCyan.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            if (step.deliverables.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PHASE DELIVERABLES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                step.deliverables.forEach { del ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = del,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
