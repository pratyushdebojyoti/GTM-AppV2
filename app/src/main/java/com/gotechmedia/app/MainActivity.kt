package com.gotechmedia.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.notification.FcmTokenManager
import com.gotechmedia.app.core.notification.NotificationDeepLinkRouter
import com.gotechmedia.app.di.DefaultAppContainer
import com.gotechmedia.app.di.ProvideAppContainer
import com.gotechmedia.app.navigation.GoTechNavGraph
import com.gotechmedia.app.ui.theme.GoTechMediaTheme
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Single-Activity entry point for GoTech Media.
 * Configures edge-to-edge system insets, theme boundaries, deep-link routing from
 * FCM notifications, and dependency injection provision.
 */
class MainActivity : ComponentActivity() {

    private var targetDeepLinkRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as? GoTechApplication)?.appContainer
            ?: DefaultAppContainer(applicationContext)

        // Log app_open analytics event
        appContainer.analyticsHelper.logAppOpen()

        // Parse deep link from starting Intent if launched from system notification or URL
        handleIncomingIntent(intent)

        // Monitor auth state and synchronize device token with authenticated user profile
        lifecycleScope.launch {
            appContainer.authRepository.authState.collectLatest { authState ->
                val userId = (authState as? AuthState.Authenticated)?.user?.id
                FcmTokenManager.syncDeviceToken(
                    context = applicationContext,
                    notificationRepository = appContainer.notificationRepository,
                    userId = userId,
                    scope = lifecycleScope
                )
            }
        }

        setContent {
            GoTechMediaTheme {
                ProvideAppContainer(container = appContainer) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = ObsidianCanvas
                    ) {
                        GoTechNavGraph(
                            incomingDeepLink = targetDeepLinkRoute,
                            onDeepLinkHandled = { targetDeepLinkRoute = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val route = NotificationDeepLinkRouter.resolveRouteFromIntent(intent)
        if (route != null) {
            targetDeepLinkRoute = route
        }
    }
}

