package com.gotechmedia.app.presentation.screens.client

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.di.ViewModelFactoryProvider
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.ProjectStatus
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun ClientProjectsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProjectDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val viewModel: ClientDashboardViewModel = viewModel(
        factory = ViewModelFactoryProvider.provideClientDashboardViewModelFactory(container)
    )

    val projectsState by viewModel.projectsState.collectAsState()
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val filterTabs = listOf(
        "ALL" to "All",
        ProjectStatus.DEVELOPMENT.displayName to "Development",
        ProjectStatus.TESTING.displayName to "Testing",
        ProjectStatus.PLANNING.displayName to "Planning",
        ProjectStatus.COMPLETED.displayName to "Completed",
        ProjectStatus.ON_HOLD.displayName to "On Hold"
    )

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Client Projects",
                subtitle = "Authorized Portfolio Index",
                onBackClick = onNavigateBack
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("client_projects_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = filterTabs.indexOfFirst { it.first.equals(selectedStatusFilter, ignoreCase = true) }.coerceAtLeast(0),
                containerColor = ObsidianSurface,
                contentColor = ElectricCyan,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    val idx = filterTabs.indexOfFirst { it.first.equals(selectedStatusFilter, ignoreCase = true) }.coerceAtLeast(0)
                    if (idx < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[idx]),
                            color = ElectricCyan
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("projects_filter_tab_row")
            ) {
                filterTabs.forEach { (key, label) ->
                    val isSelected = selectedStatusFilter.equals(key, ignoreCase = true)
                    Tab(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = key },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) ElectricCyan else TextMuted
                            )
                        }
                    )
                }
            }

            // Projects Content
            when (val res = projectsState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricCyan)
                    }
                }

                is Resource.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading projects: ${res.message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFF5252)
                        )
                    }
                }

                is Resource.Success -> {
                    val filtered = remember(res.data, selectedStatusFilter) {
                        if (selectedStatusFilter == "ALL") {
                            res.data
                        } else {
                            res.data.filter {
                                it.status.equals(selectedStatusFilter, ignoreCase = true)
                            }
                        }
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Folder,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No projects in this category.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Change filter to view all active client engagements.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("projects_filtered_list"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filtered, key = { it.id }) { project ->
                                ProjectDashboardCard(
                                    project = project,
                                    onClick = { onNavigateToProjectDetails(project.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
