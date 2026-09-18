package com.gotechmedia.app.core.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Manages device FCM registration tokens, topic subscriptions, and system push permissions.
 */
object FcmTokenManager {
    private const val TAG = "FcmTokenManager"
    private const val PREFS_NAME = "gotech_notification_prefs"
    private const val KEY_CACHED_TOKEN = "cached_fcm_token"

    private val _deviceToken = MutableStateFlow<String?>(null)
    val deviceToken: StateFlow<String?> = _deviceToken.asStateFlow()

    private val _isTokenSyncing = MutableStateFlow(false)
    val isTokenSyncing: StateFlow<Boolean> = _isTokenSyncing.asStateFlow()

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    /**
     * Checks if notification permission is granted.
     */
    fun checkPermission(context: Context): Boolean {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        _hasPermission.value = granted
        return granted
    }

    /**
     * Initializes device registration, subscribes to default broadcast topic,
     * and syncs token with user profile if authenticated.
     */
    fun syncDeviceToken(
        context: Context,
        notificationRepository: NotificationRepository,
        userId: String? = null,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ) {
        scope.launch {
            _isTokenSyncing.value = true
            try {
                // Enable messaging auto-init dynamically once initialized
                try {
                    FirebaseMessaging.getInstance().isAutoInitEnabled = true
                } catch (e: Exception) {
                    Log.w(TAG, "AutoInit enable notice: ${e.message}")
                }

                val token = try {
                    FirebaseMessaging.getInstance().token.await()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to retrieve FCM token from instance: ${e.message}")
                    null
                }

                if (!token.isNullOrBlank()) {
                    _deviceToken.value = token
                    saveCachedToken(context, token)

                    // Subscribe to global announcements
                    try {
                        FirebaseMessaging.getInstance().subscribeToTopic("all_announcements").await()
                        FirebaseMessaging.getInstance().subscribeToTopic("agency_updates").await()
                    } catch (e: Exception) {
                        Log.w(TAG, "Topic subscription notice: ${e.message}")
                    }

                    // Sync token to user profile if user is authenticated
                    if (!userId.isNullOrBlank()) {
                        notificationRepository.updateUserFcmToken(userId, token).collectLatest { res ->
                            if (res is Resource.Success) {
                                Log.i(TAG, "Successfully synced FCM token for user $userId")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error synchronizing device token", e)
            } finally {
                _isTokenSyncing.value = false
            }
        }
    }

    private fun saveCachedToken(context: Context, token: String) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_CACHED_TOKEN, token).apply()
        } catch (_: Exception) {}
    }

    fun getCachedToken(context: Context): String? {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getString(KEY_CACHED_TOKEN, null)
        } catch (_: Exception) {
            null
        }
    }
}
