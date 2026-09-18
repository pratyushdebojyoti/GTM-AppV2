package com.gotechmedia.app.presentation.screens.portfolio

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
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
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.presentation.base.UiState
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.EmptyStateView
import com.gotechmedia.app.presentation.common.ErrorStateView
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.LoadingStateView
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel,
    onItemClick: (String) -> Unit,
    onQuoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val filters = listOf("All", "Mobile", "Web", "AI", "Design")

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Portfolio",
                subtitle = "Architectural Case Studies",
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                            .clickable { onQuoteClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("portfolio_quote_cta")
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
        modifier = modifier.testTag("portfolio_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) ElectricCyan else Color.White.copy(alpha = 0.05f))
                            .border(1.dp, if (isSelected) ElectricCyan else GlassBorderSubtle, CircleShape)
                            .clickable { viewModel.setFilter(filter) }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ObsidianCanvas else Color.White
                        )
                    }
                }
            }

            when (val uiState = state) {
                is UiState.Loading -> {
                    LoadingStateView(message = "Loading case studies...")
                }
                is UiState.Empty -> {
                    EmptyStateView(
                        title = "No Projects Found",
                        description = "Case studies in this category will appear here soon.",
                        actionText = "Reset Filter",
                        onActionClick = { viewModel.setFilter("All") }
                    )
                }
                is UiState.Error -> {
                    ErrorStateView(
                        message = uiState.message,
                        onRetry = { viewModel.retry() }
                    )
                }
                is UiState.Success -> {
                    val projectList = uiState.data
                    if (projectList.isEmpty()) {
                        EmptyStateView(
                            title = "No Matching Projects",
                            description = "No case study found for '$selectedFilter'.",
                            actionText = "View All",
                            onActionClick = { viewModel.setFilter("All") }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(projectList, key = { it.id }) { item ->
                                PortfolioCardItem(
                                    item = item,
                                    onClick = { onItemClick(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioCardItem(
    item: PortfolioItem,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("portfolio_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tech Stack preview chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.techStack.take(3).forEach { tech ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tech,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inspect Technical Architecture",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
