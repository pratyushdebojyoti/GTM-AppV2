package com.gotechmedia.app

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.gotechmedia.app.di.AppContainer
import com.gotechmedia.app.di.DefaultAppContainer

/**
 * GoTech Media Application Class.
 * Initializes and scopes application-level dependencies and dependency injection container.
 */
class GoTechApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)

        // Ensure FCM auto-init does not crash or spam errors in emulated or restricted environments
        try {
            FirebaseMessaging.getInstance().isAutoInitEnabled = false
        } catch (e: Exception) {
            Log.w("GoTechApp", "FirebaseMessaging auto-init configuration skipped: ${e.message}")
        }
    }
}
