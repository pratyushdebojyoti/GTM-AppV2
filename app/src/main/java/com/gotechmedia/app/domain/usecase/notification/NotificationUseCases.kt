package com.gotechmedia.app.domain.usecase.notification

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Real-time observer for notifications addressed to the specified user or broadcast to ALL.
 */
class ObserveUserNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<Resource<List<AgencyNotification>>> =
        notificationRepository.observeUserNotifications(userId)
}

/**
 * Mark a specific notification document as read.
 */
class MarkNotificationAsReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificationId: String): Flow<Resource<Unit>> =
        notificationRepository.markAsRead(notificationId)
}

/**
 * Mark all notifications for the given user as read in a single batch.
 */
class MarkAllNotificationsAsReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<Resource<Unit>> =
        notificationRepository.markAllAsRead(userId)
}

/**
 * Delete or dismiss a notification document.
 */
class DeleteNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificationId: String): Flow<Resource<Unit>> =
        notificationRepository.deleteNotification(notificationId)
}

/**
 * Register or refresh the device's FCM token in Firestore.
 */
class RegisterDeviceTokenUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String, token: String): Flow<Resource<Unit>> =
        notificationRepository.updateUserFcmToken(userId, token)
}

/**
 * Dispatch or schedule an in-app agency notification.
 */
class CreateNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notification: AgencyNotification): Flow<Resource<String>> =
        notificationRepository.createNotification(notification)
}
