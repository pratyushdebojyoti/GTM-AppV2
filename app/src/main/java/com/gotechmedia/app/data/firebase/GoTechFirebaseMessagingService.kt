package com.gotechmedia.app.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gotechmedia.app.MainActivity
import com.gotechmedia.app.core.notification.FcmTokenManager
import com.gotechmedia.app.core.notification.NotificationDeepLinkRouter
import com.gotechmedia.app.domain.model.NotificationType

/**
 * Production-ready Firebase Messaging Service for GoTech Media.
 * Handles incoming push payloads for all 6 notification types, token registration,
 * token refreshes, and pending intent deep links.
 */
class GoTechFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Token: $token")
        try {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                val now = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("users").document(currentUserId)
                    .set(
                        mapOf(
                            "fcmToken" to token,
                            "updatedAtEpoch" to now,
                            "lastActiveEpoch" to now
                        ),
                        SetOptions.merge()
                    )

                try {
                    FirebaseFirestore.getInstance().collection("users").document(currentUserId)
                        .collection("fcmTokens").document(token)
                        .set(
                            mapOf(
                                "token" to token,
                                "platform" to "ANDROID",
                                "registeredAtEpoch" to now,
                                "lastSeenEpoch" to now
                            ),
                            SetOptions.merge()
                        )
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to sync refreshed FCM token: ${e.message}")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Incoming push message from: ${remoteMessage.from}")

        val data = remoteMessage.data

        // Extract attributes
        val rawType = data["type"] ?: "GENERAL_ANNOUNCEMENT"
        val notifType = NotificationType.fromString(rawType)

        val title = remoteMessage.notification?.title
            ?: data["title"]
            ?: notifType.defaultTitle

        val body = remoteMessage.notification?.body
            ?: data["body"]
            ?: "You have received an update from GoTech Media."

        val deepLink = data["deepLink"] ?: data["targetUrl"]
        val projectId = data["projectId"]
        val tabIndexStr = data["tab"] ?: data["tabIndex"]
        val tabIndex = tabIndexStr?.toIntOrNull() ?: -1
        val notificationId = data["id"] ?: data["notificationId"] ?: System.currentTimeMillis().toString()

        showSystemNotification(
            title = title,
            message = body,
            type = notifType,
            deepLink = deepLink,
            projectId = projectId,
            tabIndex = tabIndex,
            notificationId = notificationId
        )
    }

    private fun showSystemNotification(
        title: String,
        message: String,
        type: NotificationType,
        deepLink: String?,
        projectId: String?,
        tabIndex: Int,
        notificationId: String
    ) {
        val channelId = CHANNEL_ID_HIGH_PRIORITY
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "GoTech Media Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Sprint milestones, client messages, ticket resolutions, and agency announcements."
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Construct intent directed to MainActivity with all deep linking parameters
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_id", notificationId)
            putExtra("notification_type", type.code)
            putExtra("deep_link", deepLink)
            putExtra("project_id", projectId)
            putExtra("tab_index", tabIndex)

            // If deep link URI is formatted, attach as Intent Data
            if (!deepLink.isNullOrBlank()) {
                try {
                    data = Uri.parse(deepLink)
                } catch (_: Exception) {}
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(
                when (type) {
                    NotificationType.NEW_MESSAGE -> NotificationCompat.CATEGORY_MESSAGE
                    NotificationType.SUPPORT_TICKET_UPDATE -> NotificationCompat.CATEGORY_STATUS
                    NotificationType.PROJECT_UPDATE, NotificationType.MILESTONE_UPDATE -> NotificationCompat.CATEGORY_EVENT
                    else -> NotificationCompat.CATEGORY_RECOMMENDATION
                }
            )
            .build()

        notificationManager.notify(notificationId.hashCode(), notification)
    }

    companion object {
        private const val TAG = "GoTechFCM"
        const val CHANNEL_ID_HIGH_PRIORITY = "gotech_agency_channel"
    }
}

