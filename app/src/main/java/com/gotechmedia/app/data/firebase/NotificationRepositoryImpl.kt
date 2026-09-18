package com.gotechmedia.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/**
 * Production implementation of NotificationRepository using Firebase Cloud Messaging (FCM)
 * and Cloud Firestore notification documents.
 */
class NotificationRepositoryImpl(
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotificationRepository {

    override fun getFcmToken(): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val token = messaging.token.await()
            emit(Resource.Success(token))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to retrieve FCM push token."))
        }
    }.flowOn(ioDispatcher)

    override fun subscribeToTopic(topic: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            messaging.subscribeToTopic(topic).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to subscribe to push topic: $topic"))
        }
    }.flowOn(ioDispatcher)

    override fun unsubscribeFromTopic(topic: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            messaging.unsubscribeFromTopic(topic).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to unsubscribe from topic: $topic"))
        }
    }.flowOn(ioDispatcher)

    override fun updateUserFcmToken(userId: String, token: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val now = System.currentTimeMillis()
            firestore.collection("users").document(userId)
                .set(
                    mapOf(
                        "fcmToken" to token,
                        "updatedAtEpoch" to now,
                        "lastActiveEpoch" to now
                    ),
                    SetOptions.merge()
                )
                .await()

            // Also record device token in fcmTokens subcollection for multi-device push audit
            try {
                firestore.collection("users").document(userId)
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
                    .await()
            } catch (_: Exception) {
                // Non-fatal subcollection write
            }

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update user push token."))
        }
    }.flowOn(ioDispatcher)

    override fun observeUserNotifications(userId: String): Flow<Resource<List<AgencyNotification>>> = callbackFlow {
        trySend(Resource.Loading)
        val recipients = if (userId.isNotBlank()) listOf(userId, "ALL") else listOf("ALL")
        val listener = firestore.collection("notifications")
            .whereIn("recipientId", recipients)
            .orderBy("timestampEpoch", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Failed to listen for notifications"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        val data = doc.data ?: emptyMap()
                        val isRead = (data["read"] as? Boolean)
                            ?: (data["isRead"] as? Boolean)
                            ?: false
                        val timeEpoch = (data["timestampEpoch"] as? Number)?.toLong()
                            ?: (data["createdAtEpoch"] as? Number)?.toLong()
                            ?: System.currentTimeMillis()
                        val deepLinkStr = (data["deepLink"] as? String)
                            ?: (data["targetUrl"] as? String)
                        @Suppress("UNCHECKED_CAST")
                        val meta = (data["metadata"] as? Map<String, String>) ?: emptyMap()

                        AgencyNotification(
                            id = doc.id,
                            recipientId = data["recipientId"] as? String ?: userId,
                            title = data["title"] as? String ?: "Notification",
                            body = data["body"] as? String ?: "",
                            type = data["type"] as? String ?: com.gotechmedia.app.domain.model.NotificationType.GENERAL_ANNOUNCEMENT.code,
                            read = isRead,
                            timestampEpoch = timeEpoch,
                            deepLink = deepLinkStr,
                            metadata = meta
                        )
                    }
                    trySend(Resource.Success(list))
                }
            }
        awaitClose { listener.remove() }
    }.flowOn(ioDispatcher)

    override fun markAsRead(notificationId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            firestore.collection("notifications").document(notificationId)
                .update(
                    mapOf(
                        "read" to true,
                        "isRead" to true,
                        "readAtEpoch" to System.currentTimeMillis()
                    )
                )
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update notification."))
        }
    }.flowOn(ioDispatcher)

    override fun markAllAsRead(userId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val recipients = if (userId.isNotBlank()) listOf(userId, "ALL") else listOf("ALL")
            val snap = firestore.collection("notifications")
                .whereIn("recipientId", recipients)
                .get()
                .await()

            val batch = firestore.batch()
            val now = System.currentTimeMillis()
            var count = 0
            for (doc in snap.documents) {
                val isRead = (doc.getBoolean("read") ?: doc.getBoolean("isRead") ?: false)
                if (!isRead) {
                    batch.update(doc.reference, mapOf("read" to true, "isRead" to true, "readAtEpoch" to now))
                    count++
                }
            }
            if (count > 0) {
                batch.commit().await()
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to mark all notifications as read."))
        }
    }.flowOn(ioDispatcher)

    override fun deleteNotification(notificationId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            firestore.collection("notifications").document(notificationId)
                .delete()
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete notification."))
        }
    }.flowOn(ioDispatcher)

    override fun createNotification(notification: AgencyNotification): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val docRef = if (notification.id.isNotBlank()) {
                firestore.collection("notifications").document(notification.id)
            } else {
                firestore.collection("notifications").document()
            }
            val data = hashMapOf(
                "id" to docRef.id,
                "recipientId" to notification.recipientId,
                "title" to notification.title,
                "body" to notification.body,
                "type" to notification.type,
                "read" to notification.read,
                "isRead" to notification.read,
                "timestampEpoch" to notification.timestampEpoch,
                "createdAtEpoch" to notification.timestampEpoch,
                "deepLink" to notification.deepLink,
                "targetUrl" to notification.deepLink,
                "metadata" to notification.metadata
            )
            docRef.set(data).await()
            emit(Resource.Success(docRef.id))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to persist notification."))
        }
    }.flowOn(ioDispatcher)
}
