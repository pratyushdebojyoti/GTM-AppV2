package com.gotechmedia.app.presentation.screens.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.core.notification.NotificationDeepLinkRouter
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.NotificationType
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.EmptyStateView
import com.gotechmedia.app.presentation.common.ErrorStateView
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.LoadingStateView
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.GlassSurface
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateDeepLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Notifications",
                subtitle = if (state.unreadCount > 0) "${state.unreadCount} unread alerts" else "All alerts caught up",
                onBackClick = onNavigateBack,
                actions = {
                    if (state.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricCyan.copy(alpha = 0.12f))
                                .clickable { viewModel.markAllAsRead() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("mark_all_read_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Mark all read",
                                    color = ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("notifications_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Push Notification Permission Warning Banner (if disabled on Android 13+)
            if (!state.isPushPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationPermissionBanner(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }

            // Push Token Active Indicator Pill (Informational audit status)
            DevicePushStatusRow(
                isSyncing = state.isTokenSyncing,
                hasToken = !state.deviceToken.isNullOrBlank()
            )

            // Horizontal Filter Chips
            NotificationFilterPills(
                selectedFilter = state.selectedFilter,
                onSelectFilter = { viewModel.setFilter(it) },
                unreadCount = state.unreadCount
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading && state.notifications.isEmpty() -> {
                    LoadingStateView(
                        message = "Loading real-time notifications...",
                        modifier = Modifier.weight(1f)
                    )
                }

                state.errorMessage != null && state.notifications.isEmpty() -> {
                    ErrorStateView(
                        message = state.errorMessage ?: "Failed to sync notifications.",
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.weight(1f)
                    )
                }

                state.filteredNotifications.isEmpty() -> {
                    EmptyStateView(
                        title = if (state.selectedFilter == NotificationFilter.UNREAD) "No Unread Alerts" else "Notification Feed Empty",
                        description = if (state.selectedFilter == NotificationFilter.UNREAD)
                            "You have acknowledged all active communications."
                        else
                            "Real-time project updates, sprint milestones, and agency notices will appear here.",
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("notifications_list")
                    ) {
                        items(
                            items = state.filteredNotifications,
                            key = { it.id.ifBlank { it.timestampEpoch.toString() } }
                        ) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = {
                                    if (!notification.read) {
                                        viewModel.markAsRead(notification.id)
                                    }
                                    val resolvedRoute = NotificationDeepLinkRouter.parseStringLink(notification.deepLink.orEmpty())
                                        ?: resolveDefaultRoute(notification)
                                    if (resolvedRoute != null) {
                                        onNavigateDeepLink(resolvedRoute)
                                    }
                                },
                                onDismiss = {
                                    viewModel.deleteNotification(notification.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationPermissionBanner(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFEAB308).copy(alpha = 0.12f))
            .border(1.dp, Color(0xFFEAB308).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = Color(0xFFFDE047),
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Push Notifications Disabled",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enable notifications to receive instant milestone and ticket updates.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEAB308))
                    .clickable { onRequestPermission() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("enable_push_permission_button")
            ) {
                Text(
                    text = "Enable",
                    color = Color(0xFF0F172A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DevicePushStatusRow(
    isSyncing: Boolean,
    hasToken: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (hasToken) ElectricCyan else Color.Gray)
            )
            Text(
                text = if (hasToken) "Cloud Messaging Push Active" else "FCM Registration Pending",
                fontSize = 10.sp,
                color = TextMuted
            )
        }

        if (isSyncing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(10.dp),
                    strokeWidth = 1.5.dp,
                    color = ElectricCyan
                )
                Text(
                    text = "Syncing...",
                    fontSize = 10.sp,
                    color = ElectricCyan
                )
            }
        }
    }
}

@Composable
private fun NotificationFilterPills(
    selectedFilter: NotificationFilter,
    onSelectFilter: (NotificationFilter) -> Unit,
    unreadCount: Int
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(NotificationFilter.entries) { filter ->
            val isSelected = filter == selectedFilter
            val label = if (filter == NotificationFilter.UNREAD && unreadCount > 0) {
                "Unread ($unreadCount)"
            } else {
                filter.displayName
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) ElectricCyan.copy(alpha = 0.2f) else GlassSurface
                    )
                    .border(
                        1.dp,
                        if (isSelected) ElectricCyan.copy(alpha = 0.6f) else GlassBorderSubtle,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectFilter(filter) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("filter_pill_${filter.name.lowercase()}")
            ) {
                Text(
                    text = label,
                    color = if (isSelected) ElectricCyan else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AgencyNotification,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val notifType = notification.notificationType
    val (icon, badgeColor) = getNotificationVisuals(notifType)

    val isUnread = !notification.read

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isUnread) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            badgeColor.copy(alpha = 0.08f),
                            ObsidianSurface
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            GlassSurface,
                            ObsidianSurface
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (isUnread) badgeColor.copy(alpha = 0.35f) else GlassBorderSubtle,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("notification_item_${notification.id}")
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type Icon with Circular Glass Frame
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.16f))
                    .border(1.dp, badgeColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = notifType.displayName,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Main Notification Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = notifType.displayName.uppercase(),
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        if (isUnread) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            )
                        }
                    }

                    // Timestamp
                    Text(
                        text = formatRelativeTime(notification.timestampEpoch),
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Title
                Text(
                    text = notification.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Body text
                Text(
                    text = notification.body,
                    color = if (isUnread) TextSecondary else TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Deep Link Action Hint
                val actionLabel = getActionLabel(notifType, notification.deepLink)
                if (actionLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { onClick() }
                    ) {
                        Text(
                            text = actionLabel,
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Dismiss Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("dismiss_notification_${notification.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

private fun getNotificationVisuals(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.NEW_LEAD -> Pair(Icons.Default.PersonAdd, Color(0xFF10B981)) // Emerald
        NotificationType.PROJECT_UPDATE -> Pair(Icons.Default.RocketLaunch, ElectricCyan) // Electric Cyan
        NotificationType.MILESTONE_UPDATE -> Pair(Icons.Default.Flag, Color(0xFFF59E0B)) // Amber
        NotificationType.NEW_MESSAGE -> Pair(Icons.Default.Email, Color(0xFF6366F1)) // Indigo
        NotificationType.SUPPORT_TICKET_UPDATE -> Pair(Icons.Default.Headphones, Color(0xFFF43F5E)) // Rose
        NotificationType.GENERAL_ANNOUNCEMENT -> Pair(Icons.Default.Campaign, Color(0xFFA855F7)) // Purple
    }
}

private fun getActionLabel(type: NotificationType, deepLink: String?): String? {
    if (!deepLink.isNullOrBlank()) {
        return when (type) {
            NotificationType.NEW_LEAD -> "Review Lead"
            NotificationType.PROJECT_UPDATE -> "View Project"
            NotificationType.MILESTONE_UPDATE -> "Inspect Milestone"
            NotificationType.NEW_MESSAGE -> "Reply in Chat"
            NotificationType.SUPPORT_TICKET_UPDATE -> "View Support Ticket"
            NotificationType.GENERAL_ANNOUNCEMENT -> "Read Announcement"
        }
    }
    return null
}

private fun resolveDefaultRoute(notification: AgencyNotification): String? {
    val meta = notification.metadata
    val projectId = meta["projectId"]
    val notifType = notification.notificationType
    return when (notifType) {
        NotificationType.NEW_LEAD -> "route_custom_quote"
        NotificationType.PROJECT_UPDATE -> if (!projectId.isNullOrBlank()) "route_project_details/$projectId?tab=0" else null
        NotificationType.MILESTONE_UPDATE -> if (!projectId.isNullOrBlank()) "route_project_details/$projectId?tab=1" else null
        NotificationType.NEW_MESSAGE -> if (!projectId.isNullOrBlank()) "route_project_details/$projectId?tab=4" else null
        NotificationType.SUPPORT_TICKET_UPDATE -> if (!projectId.isNullOrBlank()) "route_project_details/$projectId?tab=5" else null
        NotificationType.GENERAL_ANNOUNCEMENT -> null
    }
}

private fun formatRelativeTime(epochMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMillis
    if (diff < 60_000L) return "Just now"
    val minutes = diff / 60_000L
    if (minutes < 60) return "${minutes}m ago"
    val hours = minutes / 60
    if (hours < 24) return "${hours}h ago"
    val days = hours / 24
    if (days == 1L) return "Yesterday"
    if (days < 7) return "${days}d ago"
    val weeks = days / 7
    return "${weeks}w ago"
}
