package com.gotechmedia.app.presentation.screens.services

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
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.domain.model.AgencyService
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
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel,
    onServiceClick: (String) -> Unit,
    onQuoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val categories = listOf(
        "All",
        "Mobile",
        "Web",
        "Design",
        "Growth",
        "Intelligent Systems",
        "Strategy"
    )

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Services",
                subtitle = "14 Practice Disciplines",
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                            .clickable { onQuoteClick("") }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("services_quote_cta")
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
        modifier = modifier.testTag("services_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Search services (e.g. Android, AI, SEO)...",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
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
                        .testTag("services_search_input")
                )
            }

            // Category Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ElectricCyan else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else GlassBorderSubtle,
                                CircleShape
                            )
                            .clickable { viewModel.setCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ObsidianCanvas else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (val uiState = state) {
                is UiState.Loading -> {
                    LoadingStateView(message = "Loading practice disciplines...")
                }
                is UiState.Empty -> {
                    EmptyStateView(
                        title = "No Services Found",
                        description = "Try adjusting your filter or search query.",
                        actionText = "Reset Filters",
                        onActionClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setCategory("All")
                        }
                    )
                }
                is UiState.Error -> {
                    ErrorStateView(
                        message = uiState.message,
                        onRetry = { viewModel.retry() }
                    )
                }
                is UiState.Success -> {
                    val serviceList = uiState.data
                    if (serviceList.isEmpty()) {
                        EmptyStateView(
                            title = "No Matches Found",
                            description = "No practice discipline matched '$searchQuery'.",
                            actionText = "Clear Search",
                            onActionClick = {
                                viewModel.setSearchQuery("")
                                viewModel.setCategory("All")
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(serviceList, key = { it.id }) { service ->
                                ServiceListItem(
                                    service = service,
                                    onItemClick = { onServiceClick(service.id) },
                                    onQuoteClick = { onQuoteClick(service.id) }
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
private fun ServiceListItem(
    service: AgencyService,
    onItemClick: () -> Unit,
    onQuoteClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .testTag("service_card_${service.id}")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            TechBadge(text = service.category)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = service.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            if (service.capabilities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    service.capabilities.take(2).forEach { cap ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cap,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No fixed pricing • Custom scoping",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Text(
                    text = "Get a Custom Quote",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onQuoteClick() }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}
