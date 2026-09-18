package com.gotechmedia.app.presentation.screens.portfoliodetails

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
fun PortfolioDetailsScreen(
    viewModel: PortfolioDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Case Study",
                subtitle = "Architecture Breakdown",
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            if (state is UiState.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianCanvas.copy(alpha = 0.95f))
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(20.dp)
                ) {
                    PrimaryCtaButton(
                        text = "Inquire About Similar Architecture",
                        onClick = { onNavigateToQuote("") },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "portfolio_details_inquire_cta"
                    )
                }
            }
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("portfolio_details_screen")
    ) { innerPadding ->
        when (val uiState = state) {
            is UiState.Loading -> {
                LoadingStateView(
                    message = "Loading case study details...",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Empty -> {
                EmptyStateView(
                    title = "Case Study Not Found",
                    description = "The requested architectural case study is unavailable.",
                    actionText = "Back to Portfolio",
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
                val item = uiState.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TechBadge(text = item.industry)
                                Text(
                                    text = item.category,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = item.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // Challenge & Solution Cards
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "THE CHALLENGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.challenge,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "ARCHITECTURAL SOLUTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.solution,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // Architecture Highlights
                    if (item.architectureHighlights.isNotEmpty()) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "TECHNICAL HIGHLIGHTS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    item.architectureHighlights.forEach { highlight ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 5.dp),
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
                                                text = highlight,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Tech Stack
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
                                item.techStack.forEach { tech ->
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

                    // Key Deliverables
                    if (item.deliverables.isNotEmpty()) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "DELIVERABLES PRODUCED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    item.deliverables.forEach { del ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
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
                                                text = del,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
