package com.gotechmedia.app.presentation.screens.projectdetails

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.di.ViewModelFactoryProvider
import com.gotechmedia.app.domain.model.AgencyDocument
import com.gotechmedia.app.domain.model.AgencyMessage
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.AgencyProjectMilestone
import com.gotechmedia.app.domain.model.AgencySupportTicket
import com.gotechmedia.app.domain.model.AgencyTeamMember
import com.gotechmedia.app.domain.model.MilestoneStatus
import com.gotechmedia.app.domain.model.ProjectStatus
import com.gotechmedia.app.domain.model.TicketPriority
import com.gotechmedia.app.domain.model.TicketStatus
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.presentation.screens.client.ProjectStatusChip
import com.gotechmedia.app.ui.theme.CyberBlue
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.GlassSurface
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.SoftIndigo
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    projectId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0
) {
    val container = LocalAppContainer.current
    val viewModel: ProjectDetailsViewModel = viewModel(
        factory = ViewModelFactoryProvider.provideProjectDetailsViewModelFactory(projectId, container)
    )

    val projectResource by viewModel.projectState.collectAsState()
    val milestonesResource by viewModel.milestonesState.collectAsState()
    val documentsResource by viewModel.documentsState.collectAsState()
    val messagesResource by viewModel.messagesState.collectAsState()
    val ticketsResource by viewModel.ticketsState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex.coerceIn(0, 5)) }
    val tabs = listOf("Overview", "Milestones", "Team", "Documents", "Messages", "Support Tickets")

    var showCreateTicketSheet by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        if (projectId.isNotBlank()) {
            container.analyticsHelper.logProjectView(projectId = projectId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProjectDetailsEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is ProjectDetailsEvent.TicketCreated -> {
                    showCreateTicketSheet = false
                }
                is ProjectDetailsEvent.MessageSent -> {
                    // Handled internally by message stream
                }
            }
        }
    }

    Scaffold(
        topBar = {
            val title = when (val res = projectResource) {
                is Resource.Success -> res.data?.title ?: "Project Details"
                else -> "Project Details"
            }
            AgencyTopBar(
                title = title,
                subtitle = "Authorized Enterprise Engagement",
                onBackClick = onNavigateBack
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("project_details_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = ObsidianSurface,
                contentColor = ElectricCyan,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = ElectricCyan
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_details_tab_row")
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) ElectricCyan else TextMuted
                            )
                        },
                        modifier = Modifier.testTag("tab_$tabTitle")
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> ProjectOverviewTab(projectResource = projectResource)
                1 -> ProjectMilestonesTab(milestonesResource = milestonesResource)
                2 -> ProjectTeamTab(projectResource = projectResource)
                3 -> ProjectDocumentsTab(documentsResource = documentsResource)
                4 -> ProjectMessagesTab(
                    messagesResource = messagesResource,
                    onSendMessage = { viewModel.sendMessage(it) }
                )
                5 -> ProjectTicketsTab(
                    ticketsResource = ticketsResource,
                    onCreateTicketClick = { showCreateTicketSheet = true }
                )
            }
        }
    }

    // Modal Sheet for New Support Ticket
    if (showCreateTicketSheet) {
        CreateSupportTicketBottomSheet(
            onDismiss = { showCreateTicketSheet = false },
            onSubmit = { subject, desc, priority ->
                container.analyticsHelper.logSupportTicketCreated(
                    projectId = projectId,
                    priority = priority.name
                )
                viewModel.createSupportTicket(subject, desc, priority)
            }
        )
    }
}

// =========================================================================
// TAB 0: OVERVIEW (Project Name, Description, Status, Progress, Dates, Read-Only Notice)
// =========================================================================

@Composable
fun ProjectOverviewTab(
    projectResource: Resource<AgencyProject?>,
    modifier: Modifier = Modifier
) {
    when (projectResource) {
        is Resource.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        }
        is Resource.Error -> {
            Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Failed to load project overview: ${projectResource.message}",
                    color = Color(0xFFFF5252)
                )
            }
        }
        is Resource.Success -> {
            val project = projectResource.data
            if (project == null) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Project not found.", color = TextMuted)
                }
            } else {
                val statusEnum = remember(project.status) { ProjectStatus.fromString(project.status) }

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .testTag("project_overview_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Security / Read-Only Policy Card
                    item {
                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("readonly_policy_notice")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Read-only authorization",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Client Read-Only Governance",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = "Project status, progress velocity, and assigned team architecture are strictly managed by GoTech Media admins via Firestore rules.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Project Core Details Card
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = project.title,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.weight(1f).testTag("overview_project_name")
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    ProjectStatusChip(status = statusEnum.displayName)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = project.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.testTag("overview_project_description")
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                                HorizontalDivider(color = GlassBorderSubtle)
                                Spacer(modifier = Modifier.height(16.dp))

                                // Progress Indicator
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Overall Delivery Progress",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "${project.progressPercentage}%",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan,
                                        modifier = Modifier.testTag("overview_progress_value")
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { (project.progressPercentage / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = ElectricCyan,
                                    trackColor = ObsidianSurface
                                )
                            }
                        }
                    }

                    // Timeline & Dates Card
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "PROJECT TIMELINE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp
                                    ),
                                    color = ElectricCyan
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TimelineColumn(
                                        label = "Start Date",
                                        value = project.startDate.ifBlank { "Not specified" },
                                        testTag = "overview_start_date"
                                    )
                                    TimelineColumn(
                                        label = "Expected Completion",
                                        value = project.expectedCompletion.ifBlank { project.targetDeliveryDate.ifBlank { "TBD" } },
                                        testTag = "overview_expected_completion"
                                    )
                                }
                            }
                        }
                    }

                    // Technology Architecture Stack
                    if (project.techStack.isNotEmpty()) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "TECHNICAL STACK",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp
                                        ),
                                        color = ElectricCyan
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        project.techStack.forEach { tech ->
                                            TechBadge(
                                                text = tech,
                                                accentColor = Color.White,
                                                backgroundColor = ObsidianSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineColumn(
    label: String,
    value: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            modifier = Modifier.testTag(testTag)
        )
    }
}

// =========================================================================
// TAB 1: MILESTONES (Pending / In Progress / Completed)
// =========================================================================

@Composable
fun ProjectMilestonesTab(
    milestonesResource: Resource<List<AgencyProjectMilestone>>,
    modifier: Modifier = Modifier
) {
    when (milestonesResource) {
        is Resource.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        }
        is Resource.Error -> {
            Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${milestonesResource.message}", color = Color(0xFFFF5252))
            }
        }
        is Resource.Success -> {
            val milestones = milestonesResource.data
            if (milestones.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No milestones scheduled yet.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .testTag("project_milestones_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "PROJECT DELIVERABLE MILESTONES",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    items(milestones, key = { it.id }) { milestone ->
                        MilestoneCard(milestone = milestone)
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneCard(
    milestone: AgencyProjectMilestone,
    modifier: Modifier = Modifier
) {
    val statusEnum = remember(milestone.status) {
        MilestoneStatus.fromString(milestone.status)
    }

    val (chipColor, textColor, icon) = when (statusEnum) {
        MilestoneStatus.COMPLETED -> Triple(Color(0xFF00E676).copy(alpha = 0.15f), Color(0xFF00E676), Icons.Outlined.CheckCircle)
        MilestoneStatus.IN_PROGRESS -> Triple(ElectricCyan.copy(alpha = 0.15f), ElectricCyan, Icons.Outlined.Engineering)
        MilestoneStatus.PENDING -> Triple(TextMuted.copy(alpha = 0.15f), TextMuted, Icons.Outlined.Pending)
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("milestone_card_${milestone.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ObsidianSurface)
                            .border(1.dp, GlassBorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${milestone.stepNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Milestone status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(chipColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusEnum.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            if (milestone.deliverables.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                milestone.deliverables.forEach { deliverable ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (statusEnum == MilestoneStatus.COMPLETED) Color(0xFF00E676) else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = deliverable,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            if (milestone.dueDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Target Due: ${milestone.dueDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

// =========================================================================
// TAB 2: ASSIGNED TEAM (Read-only, assigned by agency)
// =========================================================================

@Composable
fun ProjectTeamTab(
    projectResource: Resource<AgencyProject?>,
    modifier: Modifier = Modifier
) {
    when (projectResource) {
        is Resource.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        }
        is Resource.Error -> {
            Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${projectResource.message}", color = Color(0xFFFF5252))
            }
        }
        is Resource.Success -> {
            val team = projectResource.data?.assignedTeam ?: emptyList()

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("project_team_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text(
                            text = "ASSIGNED ENGINEERING & ARCHITECTURE TEAM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = ElectricCyan
                        )
                        Text(
                            text = "Dedicated GoTech Media staff provisioned for this sprint cycle (read-only)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                if (team.isEmpty()) {
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Dedicated team assignment in progress.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(team, key = { it.id }) { member ->
                        TeamMemberCard(member = member)
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMemberCard(
    member: AgencyTeamMember,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("team_member_${member.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricCyan.copy(alpha = 0.6f), SoftIndigo.copy(alpha = 0.6f))
                        )
                    )
                    .border(1.dp, GlassBorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = member.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan
                )
                if (member.email.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = member.email,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            TechBadge(
                text = "ASSIGNED",
                accentColor = ElectricCyan,
                backgroundColor = ElectricCyan.copy(alpha = 0.1f)
            )
        }
    }
}

// =========================================================================
// TAB 3: DOCUMENTS
// =========================================================================

@Composable
fun ProjectDocumentsTab(
    documentsResource: Resource<List<AgencyDocument>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    when (documentsResource) {
        is Resource.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        }
        is Resource.Error -> {
            Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = "Failed to load documents: ${documentsResource.message}", color = Color(0xFFFF5252))
            }
        }
        is Resource.Success -> {
            val documents = documentsResource.data

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("project_documents_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "PROJECT DELIVERABLES & CONTRACTUAL DOCUMENTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectricCyan,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                if (documents.isEmpty()) {
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No uploaded documents for this project.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(documents, key = { it.id }) { doc ->
                        DocumentCard(
                            document = doc,
                            onDownload = {
                                Toast.makeText(context, "Opening ${doc.title}...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(
    document: AgencyDocument,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeIcon = when (document.type) {
        "ARCHITECTURE_SPEC" -> Icons.Outlined.Engineering
        "SOW" -> Icons.Outlined.ReceiptLong
        else -> Icons.Outlined.Description
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("document_card_${document.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = typeIcon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Uploaded by ${document.uploadedBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                if (document.sizeBytes > 0) {
                    val sizeMb = String.format(Locale.US, "%.1f MB", document.sizeBytes / (1024f * 1024f))
                    Text(
                        text = "${document.type} • $sizeMb",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                }
            }

            IconButton(
                onClick = onDownload,
                modifier = Modifier.testTag("download_doc_${document.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Download document",
                    tint = ElectricCyan
                )
            }
        }
    }
}

// =========================================================================
// TAB 4: MESSAGES (Real-time Project Communications)
// =========================================================================

@Composable
fun ProjectMessagesTab(
    messagesResource: Resource<List<AgencyMessage>>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("project_messages_tab")
    ) {
        // Message History
        Box(modifier = Modifier.weight(1f)) {
            when (messagesResource) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricCyan)
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Message stream error: ${messagesResource.message}", color = Color(0xFFFF5252))
                    }
                }
                is Resource.Success -> {
                    val messages = messagesResource.data
                    if (messages.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Forum,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Direct Project Communication Channel",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Send real-time inquiries directly to your assigned engineering leads.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().testTag("messages_list"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                MessageBubble(message = msg)
                            }
                        }
                    }
                }
            }
        }

        // Input Field Bar
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Type an inquiry to your team...", color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("message_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GlassBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (messageInput.isNotBlank()) ElectricCyan else ObsidianSurface)
                        .clickable(enabled = messageInput.isNotBlank()) {
                            onSendMessage(messageInput)
                            messageInput = ""
                        }
                        .testTag("send_message_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Send message",
                        tint = if (messageInput.isNotBlank()) ObsidianCanvas else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: AgencyMessage,
    modifier: Modifier = Modifier
) {
    val isClient = message.senderRole.equals("CLIENT", ignoreCase = true)
    val alignment = if (isClient) Alignment.End else Alignment.Start
    val bubbleColor = if (isClient) ElectricCyan.copy(alpha = 0.15f) else ObsidianSurface
    val borderColor = if (isClient) ElectricCyan.copy(alpha = 0.4f) else GlassBorderSubtle

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isClient) "You" else message.senderName.ifBlank { "GoTech Team" },
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isClient) ElectricCyan else SoftIndigo
            )
            Spacer(modifier = Modifier.width(6.dp))
            val timeStr = remember(message.timestampEpoch) {
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestampEpoch))
            }
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isClient) 14.dp else 2.dp,
                        bottomEnd = if (isClient) 2.dp else 14.dp
                    )
                )
                .background(bubbleColor)
                .border(
                    1.dp,
                    borderColor,
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isClient) 14.dp else 2.dp,
                        bottomEnd = if (isClient) 2.dp else 14.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

// =========================================================================
// TAB 5: SUPPORT TICKETS (Subject, Description, Priority, Status)
// =========================================================================

@Composable
fun ProjectTicketsTab(
    ticketsResource: Resource<List<AgencySupportTicket>>,
    onCreateTicketClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("project_tickets_tab")
    ) {
        // Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SUPPORT TICKETS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = ElectricCyan
                )
                Text(
                    text = "Track SLA & submit technical requests",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Button(
                onClick = onCreateTicketClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = ObsidianCanvas
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("new_ticket_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "New Ticket",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        when (ticketsResource) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricCyan)
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Failed to load support tickets: ${ticketsResource.message}", color = Color(0xFFFF5252))
                }
            }
            is Resource.Success -> {
                val tickets = ticketsResource.data
                if (tickets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.SupportAgent,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No active support tickets.",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Submit a ticket above for priority architectural or engineering support.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("tickets_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(tickets, key = { it.id }) { ticket ->
                            SupportTicketCard(ticket = ticket)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportTicketCard(
    ticket: AgencySupportTicket,
    modifier: Modifier = Modifier
) {
    val priorityEnum = remember(ticket.priority) { TicketPriority.fromString(ticket.priority) }
    val statusEnum = remember(ticket.status) { TicketStatus.fromString(ticket.status) }

    val priorityColor = when (priorityEnum) {
        TicketPriority.HIGH -> Color(0xFFFF5252)
        TicketPriority.MEDIUM -> Color(0xFFFFB300)
        TicketPriority.LOW -> ElectricCyan
    }

    val statusColor = when (statusEnum) {
        TicketStatus.OPEN -> ElectricCyan
        TicketStatus.IN_PROGRESS -> Color(0xFFFFB300)
        TicketStatus.RESOLVED -> Color(0xFF00E676)
        TicketStatus.CLOSED -> TextMuted
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ticket_card_${ticket.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(priorityColor.copy(alpha = 0.15f))
                        .border(1.dp, priorityColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("ticket_priority_${ticket.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${priorityEnum.displayName.uppercase()} PRIORITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = priorityColor
                    )
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("ticket_status_${ticket.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusEnum.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = ticket.subject,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.testTag("ticket_subject_${ticket.id}")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.testTag("ticket_desc_${ticket.id}")
            )

            Spacer(modifier = Modifier.height(12.dp))

            val dateStr = remember(ticket.createdAtEpoch) {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(ticket.createdAtEpoch))
            }
            Text(
                text = "Submitted on $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

// =========================================================================
// BOTTOM SHEET: CREATE SUPPORT TICKET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSupportTicketBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (subject: String, description: String, priority: TicketPriority) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TicketPriority.MEDIUM) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .testTag("create_ticket_sheet")
        ) {
            Text(
                text = "Submit Enterprise Support Ticket",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Describe the request, bug, or specification clarification.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Subject
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                placeholder = { Text("e.g. Firebase Auth token renewal question") },
                modifier = Modifier.fillMaxWidth().testTag("ticket_subject_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = GlassBorderSubtle,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = ObsidianSurface,
                    unfocusedContainerColor = ObsidianSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Detail the issue, expected outcome, or question...") },
                modifier = Modifier.fillMaxWidth().height(120.dp).testTag("ticket_desc_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = GlassBorderSubtle,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = ObsidianSurface,
                    unfocusedContainerColor = ObsidianSurface
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Selection
            Text(
                text = "Priority Level",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TicketPriority.entries.forEach { p ->
                    val isSelected = priority == p
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElectricCyan.copy(alpha = 0.2f) else ObsidianSurface)
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else GlassBorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { priority = p }
                            .padding(vertical = 10.dp)
                            .testTag("priority_option_${p.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) ElectricCyan else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryCtaButton(
                text = "Submit Ticket",
                onClick = { onSubmit(subject, description, priority) },
                enabled = subject.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.fillMaxWidth().testTag("submit_ticket_confirm_btn")
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
