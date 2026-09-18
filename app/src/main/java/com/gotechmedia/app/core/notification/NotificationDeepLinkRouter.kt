package com.gotechmedia.app.core.notification

import android.content.Intent
import android.net.Uri
import com.gotechmedia.app.domain.model.NotificationType
import com.gotechmedia.app.navigation.Screen

/**
 * Intelligent Deep Link Router for Push Notifications and System Intents.
 * Translates FCM payloads, Android URIs (e.g. `gotech://...`), and custom bundles
 * into type-safe Compose navigation routes.
 */
object NotificationDeepLinkRouter {

    const val SCHEME = "gotech"
    const val HOST_PROJECT = "project"
    const val HOST_NOTIFICATIONS = "notifications"
    const val HOST_LEAD = "lead"
    const val HOST_QUOTE = "quote"
    const val HOST_DASHBOARD = "dashboard"

    /**
     * Resolves an incoming Android Intent into a target Compose route string.
     */
    fun resolveRouteFromIntent(intent: Intent?): String? {
        if (intent == null) return null

        // 1. Direct deep link URI check (e.g. gotech://project/123?tab=1)
        intent.data?.let { uri ->
            val route = parseUri(uri)
            if (route != null) return route
        }

        // 2. Explicit string extra deep_link or target_url
        val rawDeepLink = intent.getStringExtra("deep_link")
            ?: intent.getStringExtra("target_url")
            ?: intent.getStringExtra("target_route")
        if (!rawDeepLink.isNullOrBlank()) {
            val route = parseStringLink(rawDeepLink)
            if (route != null) return route
        }

        // 3. Fallback: Parse from explicit notification extras
        val projectId = intent.getStringExtra("project_id")
        val tabIndex = intent.getIntExtra("tab_index", -1)
        val notifTypeRaw = intent.getStringExtra("notification_type")
        val type = if (notifTypeRaw != null) NotificationType.fromString(notifTypeRaw) else null

        if (!projectId.isNullOrBlank()) {
            val finalTab = if (tabIndex >= 0) {
                tabIndex
            } else {
                when (type) {
                    NotificationType.MILESTONE_UPDATE -> 1
                    NotificationType.NEW_MESSAGE -> 4
                    NotificationType.SUPPORT_TICKET_UPDATE -> 5
                    else -> 0
                }
            }
            return Screen.ProjectDetails.createRoute(projectId, finalTab)
        }

        if (type == NotificationType.NEW_LEAD) {
            return Screen.CustomQuote.createRoute()
        }

        if (type == NotificationType.GENERAL_ANNOUNCEMENT) {
            return Screen.Notifications.route
        }

        return null
    }

    /**
     * Parses a string representation of a deep link or URI.
     */
    fun parseStringLink(link: String): String? {
        val trimmed = link.trim()
        if (trimmed.isEmpty()) return null

        // If it starts with route_, return directly
        if (trimmed.startsWith("route_")) {
            return trimmed
        }

        return try {
            val uri = Uri.parse(trimmed)
            parseUri(uri)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Parses a structured Uri into a Compose navigation route.
     */
    fun parseUri(uri: Uri): String? {
        val scheme = uri.scheme?.lowercase()
        val host = uri.host?.lowercase()
        val pathSegments = uri.pathSegments

        if (scheme == SCHEME || scheme == "https" || scheme == "http") {
            // E.g. gotech://notifications OR https://gotechmedia.com/notifications
            if (host == HOST_NOTIFICATIONS || (pathSegments.isNotEmpty() && pathSegments[0].equals("notifications", ignoreCase = true))) {
                return Screen.Notifications.route
            }

            // E.g. gotech://project/{projectId}?tab=1
            if (host == HOST_PROJECT) {
                val projectId = pathSegments.firstOrNull()
                val tab = uri.getQueryParameter("tab")?.toIntOrNull() ?: 0
                if (!projectId.isNullOrBlank()) {
                    return Screen.ProjectDetails.createRoute(projectId, tab)
                }
            }

            // Path-based project matching (e.g. gotech://app/project/{id} or https://gotechmedia.com/project/{id})
            if (pathSegments.size >= 2 && pathSegments[0].equals("project", ignoreCase = true)) {
                val projectId = pathSegments[1]
                val tab = uri.getQueryParameter("tab")?.toIntOrNull() ?: 0
                return Screen.ProjectDetails.createRoute(projectId, tab)
            }

            // E.g. gotech://lead/{leadId} or gotech://quote
            if (host == HOST_LEAD || host == HOST_QUOTE || (pathSegments.isNotEmpty() && (pathSegments[0] == "lead" || pathSegments[0] == "quote"))) {
                return Screen.CustomQuote.createRoute()
            }

            // E.g. gotech://dashboard
            if (host == HOST_DASHBOARD || (pathSegments.isNotEmpty() && pathSegments[0] == "dashboard")) {
                return Screen.ClientDashboard.route
            }
        }

        return null
    }

    /**
     * Helper to construct canonical deep link string for a notification type.
     */
    fun buildDeepLink(
        type: NotificationType,
        projectId: String? = null,
        tabIndex: Int? = null,
        leadId: String? = null
    ): String {
        return when (type) {
            NotificationType.NEW_LEAD -> {
                if (!leadId.isNullOrBlank()) "$SCHEME://$HOST_LEAD/$leadId" else "$SCHEME://$HOST_QUOTE"
            }
            NotificationType.PROJECT_UPDATE -> {
                if (!projectId.isNullOrBlank()) "$SCHEME://$HOST_PROJECT/$projectId?tab=${tabIndex ?: 0}" else "$SCHEME://$HOST_NOTIFICATIONS"
            }
            NotificationType.MILESTONE_UPDATE -> {
                if (!projectId.isNullOrBlank()) "$SCHEME://$HOST_PROJECT/$projectId?tab=${tabIndex ?: 1}" else "$SCHEME://$HOST_NOTIFICATIONS"
            }
            NotificationType.NEW_MESSAGE -> {
                if (!projectId.isNullOrBlank()) "$SCHEME://$HOST_PROJECT/$projectId?tab=${tabIndex ?: 4}" else "$SCHEME://$HOST_NOTIFICATIONS"
            }
            NotificationType.SUPPORT_TICKET_UPDATE -> {
                if (!projectId.isNullOrBlank()) "$SCHEME://$HOST_PROJECT/$projectId?tab=${tabIndex ?: 5}" else "$SCHEME://$HOST_NOTIFICATIONS"
            }
            NotificationType.GENERAL_ANNOUNCEMENT -> {
                "$SCHEME://$HOST_NOTIFICATIONS"
            }
        }
    }
}
