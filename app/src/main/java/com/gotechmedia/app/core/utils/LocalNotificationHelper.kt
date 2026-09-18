package com.gotechmedia.app.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.gotechmedia.app.MainActivity

/**
 * Utility to post high-priority local status notifications for customer actions
 * such as Quote Submission, Milestone Completion, and Ticket Creation.
 */
object LocalNotificationHelper {

    private const val CHANNEL_ID = "gotech_agency_channel"
    private const val CHANNEL_NAME = "GoTech Media Communications"

    fun showQuoteReceivedNotification(
        context: Context,
        leadReferenceId: String,
        companyName: String
    ) {
        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return

            // Ensure Notification Channel exists (Android O+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Updates on your custom quotes and software development sprints"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Check permission on Android 13+ (Tiramisu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = "Quote Request Received — $leadReferenceId"
            val text = if (companyName.isNotBlank()) {
                "We received your project requirements for $companyName. A Solutions Architect will review it within 24h."
            } else {
                "We received your project requirements. A Solutions Architect will review it within 24h."
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            notificationManager.notify(notificationId, notification)
        } catch (_: Exception) {
            // Failsafe: Never crash app if system notification service is unavailable
        }
    }
}
