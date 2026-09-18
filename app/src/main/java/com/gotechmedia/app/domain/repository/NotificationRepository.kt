package com.gotechmedia.app.domain.repository

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyNotification
import kotlinx.coroutines.flow.Flow

/**
 * Push Notification & FCM Repository abstraction.
 * Provides topic subscriptions, device registration tokens, and user notification feeds.
 */
interface NotificationRepository {

    /**
     * Retrieve current device Firebase Cloud Messaging registration token.
     */
    fun getFcmToken(): Flow<Resource<String>>

    /**
     * Subscribe current device to a broadcast messaging topic (e.g. "all", "announcements").
     */
    fun subscribeToTopic(topic: String): Flow<Resource<Unit>>

    /**
     * Unsubscribe from a broadcast messaging topic.
     */
    fun unsubscribeFromTopic(topic: String): Flow<Resource<Unit>>

    /**
     * Associate the FCM token with the authenticated user profile in Firestore.
     */
    fun updateUserFcmToken(userId: String, token: String): Flow<Resource<Unit>>

    /**
     * Stream real-time notifications addressed to this user.
     */
    fun observeUserNotifications(userId: String): Flow<Resource<List<AgencyNotification>>>

    /**
     * Mark a specific notification document as read.
     */
    fun markAsRead(notificationId: String): Flow<Resource<Unit>>

    /**
     * Mark all active notifications addressed to this user as read.
     */
    fun markAllAsRead(userId: String): Flow<Resource<Unit>>

    /**
     * Delete or dismiss a specific notification.
     */
    fun deleteNotification(notificationId: String): Flow<Resource<Unit>>

    /**
     * Record a new notification in Firestore.
     */
    fun createNotification(notification: AgencyNotification): Flow<Resource<String>>
}
