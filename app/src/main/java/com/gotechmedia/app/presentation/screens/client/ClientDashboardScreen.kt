package com.gotechmedia.app.presentation.screens.client

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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.di.ViewModelFactoryProvider
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.ProjectStatus
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.presentation.common.TechBadge
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

@Composable
fun ClientDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToProjectDetails: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val viewModel: ClientDashboardViewModel = viewModel(
        factory = ViewModelFactoryProvider.provideClientDashboardViewModelFactory(container)
    )

    val authState by viewModel.authState.collectAsState()
    val projectsState by viewModel.projectsState.collectAsState()
    val notificationsState by viewModel.notificationsState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ClientDashboardEvent.SignedOut -> onSignOut()
                is ClientDashboardEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Client Dashboard",
                subtitle = "Active Enterprise Engagements",
                onBackClick = onNavigateBack,
                actions = {
                    if (authState is AuthState.Authenticated) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ObsidianSurface)
                                .clickable { viewModel.signOut() }
                                .testTag("dashboard_sign_out_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = "Sign Out",
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("client_dashboard_screen")
    ) { innerPadding ->
        when (val state = authState) {
            is AuthState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ElectricCyan,
                        modifier = Modifier.testTag("dashboard_loading_indicator")
                    )
                }
            }

            is AuthState.Unauthenticated -> {
                UnauthenticatedDashboardView(
                    onNavigateToLogin = onNavigateToLogin,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is AuthState.Error -> {
                UnauthenticatedDashboardView(
                    onNavigateToLogin = onNavigateToLogin,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is AuthState.Authenticated -> {
                AuthenticatedDashboardContent(
                    userName = state.user.displayName.ifBlank { "Valued Enterprise Partner" },
                    userEmail = state.user.email,
                    projectsResource = projectsState,
                    notificationsResource = notificationsState,
                    onViewAllProjects = onNavigateToProjects,
                    onProjectClick = onNavigateToProjectDetails,
                    onNotificationClick = { viewModel.markNotificationRead(it.id) },
                    contentPadding = innerPadding
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedDashboardContent(
    userName: String,
    userEmail: String,
    projectsResource: Resource<List<AgencyProject>>,
    notificationsResource: Resource<List<AgencyNotification>>,
    onViewAllProjects: () -> Unit,
    onProjectClick: (String) -> Unit,
    onNotificationClick: (AgencyNotification) -> Unit,
    contentPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("client_dashboard_list"),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 40.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Client Identity Header
        item {
            ClientIdentityHeaderCard(
                clientName = userName,
                clientEmail = userEmail
            )
        }

        // 2. Active Projects Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE PROJECTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectricCyan
                    )
                    Text(
                        text = "Real-time sprint progress and statuses",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Text(
                    text = "View All →",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ElectricCyan,
                    modifier = Modifier
                        .clickable { onViewAllProjects() }
                        .padding(4.dp)
                        .testTag("view_all_projects_link")
                )
            }
        }

        // 3. Projects List / States
        when (projectsResource) {
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ElectricCyan,
                            modifier = Modifier.testTag("projects_loading_spinner")
                        )
                    }
                }
            }

            is Resource.Error -> {
                item {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("projects_error_card")
                    ) {
                        Text(
                            text = "Could not sync active projects: ${projectsResource.message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            is Resource.Success -> {
                val projects = projectsResource.data
                if (projects.isEmpty()) {
                    item {
                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("no_projects_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Folder,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No active projects assigned yet.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Your dedicated team will provision your project workspace once discovery is signed.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(projects, key = { it.id }) { project ->
                        ProjectDashboardCard(
                            project = project,
                            onClick = { onProjectClick(project.id) }
                        )
                    }
                }
            }
        }

        // 4. Notifications Section Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NOTIFICATIONS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectricCyan
                    )
                    Text(
                        text = "Realtime sprint updates and milestone completions",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 5. Notifications List
        when (notificationsResource) {
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ElectricCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            is Resource.Error -> {
                item {
                    Text(
                        text = "Notifications unavailable: ${notificationsResource.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            is Resource.Success -> {
                val notifications = notificationsResource.data
                if (notifications.isEmpty()) {
                    item {
                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("no_notifications_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "All caught up. No unread notifications.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(notifications.take(5), key = { it.id }) { notif ->
                        NotificationItemCard(
                            notification = notif,
                            onClick = { onNotificationClick(notif) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClientIdentityHeaderCard(
    clientName: String,
    clientEmail: String,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("client_identity_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricCyan.copy(alpha = 0.8f), SoftIndigo.copy(alpha = 0.8f))
                        )
                    )
                    .border(1.5.dp, GlassBorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = clientName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("dashboard_client_name")
                )
                Text(
                    text = clientEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TechBadge(
                text = "ENTERPRISE",
                accentColor = ElectricCyan,
                backgroundColor = ElectricCyan.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
fun ProjectDashboardCard(
    project: AgencyProject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusEnum = remember(project.status) {
        ProjectStatus.fromString(project.status)
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("dashboard_project_card_${project.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top row: Title and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("project_title_${project.id}")
                    )
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                ProjectStatusChip(status = statusEnum.displayName)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sprint Progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = "${project.progressPercentage}%",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan,
                    modifier = Modifier.testTag("project_progress_${project.id}")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (project.progressPercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ElectricCyan,
                trackColor = ObsidianSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.HourglassBottom,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (project.expectedCompletion.isNotBlank()) "Target: ${project.expectedCompletion}" else "In progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (chipColor, textColor) = when (status.lowercase()) {
        "completed", "launch" -> Color(0xFF00E676).copy(alpha = 0.15f) to Color(0xFF00E676)
        "review", "testing" -> Color(0xFFFFB300).copy(alpha = 0.15f) to Color(0xFFFFB300)
        "on hold" -> Color(0xFFFF5252).copy(alpha = 0.15f) to Color(0xFFFF5252)
        else -> ElectricCyan.copy(alpha = 0.15f) to ElectricCyan
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(chipColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("project_status_chip_$status"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun NotificationItemCard(
    notification: AgencyNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (notification.read) ObsidianSurface else ElectricCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (notification.read) Icons.Outlined.Notifications else Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                    tint = if (notification.read) TextMuted else ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (notification.read) FontWeight.Normal else FontWeight.Bold
                    ),
                    color = if (notification.read) TextSecondary else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun UnauthenticatedDashboardView(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(ObsidianSurface)
                .border(1.dp, GlassBorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Enterprise Client Portal",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to monitor active projects, sprint velocity, deliverables, milestone sign-offs, and priority support tickets.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryCtaButton(
            text = "Sign In to Client Portal",
            onClick = onNavigateToLogin,
            icon = Icons.Outlined.Login,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_sign_in_cta")
        )
    }
}
