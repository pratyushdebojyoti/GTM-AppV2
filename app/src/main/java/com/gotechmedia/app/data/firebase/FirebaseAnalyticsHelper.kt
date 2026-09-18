package com.gotechmedia.app.data.firebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Helper class for logging structured analytics events via Firebase Analytics.
 * Strict privacy compliance: Never sends PII (e.g. personal names, phone numbers, raw emails, passwords).
 */
class FirebaseAnalyticsHelper(context: Context) {

    private val analytics: FirebaseAnalytics = try {
        FirebaseAnalytics.getInstance(context)
    } catch (e: Exception) {
        Log.w("AnalyticsHelper", "FirebaseAnalytics initialization notice: ${e.message}")
        FirebaseAnalytics.getInstance(context.applicationContext)
    }

    /**
     * Triggered on application open or initial setup.
     */
    fun logAppOpen() {
        try {
            analytics.logEvent("app_open", Bundle())
            analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle())
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logAppOpen error: ${e.message}")
        }
    }

    /**
     * Triggered when the user completes or finishes the onboarding intro walkthrough.
     */
    fun logOnboardingCompleted() {
        try {
            analytics.logEvent("onboarding_completed", Bundle())
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logOnboardingCompleted error: ${e.message}")
        }
    }

    /**
     * Triggered when a service details or catalog entry is viewed.
     */
    fun logServiceView(serviceId: String, category: String = "") {
        try {
            val bundle = Bundle().apply {
                putString("service_id", serviceId)
                if (category.isNotBlank()) putString("category", category)
            }
            analytics.logEvent("service_view", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logServiceView error: ${e.message}")
        }
    }

    /**
     * Triggered when a portfolio item or case study is viewed.
     */
    fun logPortfolioView(portfolioId: String, industry: String = "") {
        try {
            val bundle = Bundle().apply {
                putString("portfolio_id", portfolioId)
                if (industry.isNotBlank()) putString("industry", industry)
            }
            analytics.logEvent("portfolio_view", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logPortfolioView error: ${e.message}")
        }
    }

    /**
     * Triggered when quote builder is started.
     */
    fun logQuoteStarted(source: String = "request_a_quote", serviceId: String = "") {
        try {
            val bundle = Bundle().apply {
                putString("source", source)
                if (serviceId.isNotBlank()) putString("service_id", serviceId)
            }
            analytics.logEvent("quote_started", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logQuoteStarted error: ${e.message}")
        }
    }

    fun logQuoteInitiated(serviceId: String, budgetTier: String) {
        try {
            val bundle = Bundle().apply {
                putString("service_id", serviceId)
                putString("budget_tier", budgetTier)
            }
            analytics.logEvent("quote_started", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logQuoteInitiated error: ${e.message}")
        }
    }

    /**
     * Triggered when quote form is successfully submitted. No personal email/name passed.
     */
    fun logQuoteSubmitted(
        leadId: String = "",
        serviceCount: Int = 1,
        primaryService: String = "",
        budgetTier: String = "",
        timeline: String = "",
        hasAttachment: Boolean = false
    ) {
        try {
            val bundle = Bundle().apply {
                if (leadId.isNotBlank()) putString("lead_id", leadId)
                putInt("service_count", serviceCount)
                if (primaryService.isNotBlank()) putString("primary_service", primaryService)
                if (budgetTier.isNotBlank()) putString("budget_tier", budgetTier)
                if (timeline.isNotBlank()) putString("timeline", timeline)
                putBoolean("has_attachment", hasAttachment)
            }
            analytics.logEvent("quote_submitted", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logQuoteSubmitted error: ${e.message}")
        }
    }

    fun logQuoteSubmitted(serviceId: String, timeline: String, budgetTier: String) {
        logQuoteSubmitted(
            leadId = "",
            serviceCount = 1,
            primaryService = serviceId,
            budgetTier = budgetTier,
            timeline = timeline,
            hasAttachment = false
        )
    }

    /**
     * Triggered when WhatsApp channel action button is clicked.
     */
    fun logWhatsAppClicked(sourceScreen: String = "contact") {
        try {
            val bundle = Bundle().apply {
                putString("source_screen", sourceScreen)
            }
            analytics.logEvent("whatsapp_clicked", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logWhatsAppClicked error: ${e.message}")
        }
    }

    /**
     * Triggered when Call channel action button is clicked.
     */
    fun logCallClicked(sourceScreen: String = "contact") {
        try {
            val bundle = Bundle().apply {
                putString("source_screen", sourceScreen)
            }
            analytics.logEvent("call_clicked", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logCallClicked error: ${e.message}")
        }
    }

    /**
     * Triggered when Email channel action button is clicked.
     */
    fun logEmailClicked(sourceScreen: String = "contact") {
        try {
            val bundle = Bundle().apply {
                putString("source_screen", sourceScreen)
            }
            analytics.logEvent("email_clicked", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logEmailClicked error: ${e.message}")
        }
    }

    /**
     * Triggered when Website channel action button is clicked.
     */
    fun logWebsiteClicked(sourceScreen: String = "contact") {
        try {
            val bundle = Bundle().apply {
                putString("source_screen", sourceScreen)
            }
            analytics.logEvent("website_clicked", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logWebsiteClicked error: ${e.message}")
        }
    }

    /**
     * Triggered on user login.
     */
    fun logLogin(method: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
            }
            analytics.logEvent("login", bundle)
            analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logLogin error: ${e.message}")
        }
    }

    /**
     * Triggered on user signup/registration.
     */
    fun logSignUp(method: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
            }
            analytics.logEvent("signup", bundle)
            analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logSignUp error: ${e.message}")
        }
    }

    /**
     * Triggered when a project details view is accessed in client portal.
     */
    fun logProjectView(projectId: String) {
        try {
            val bundle = Bundle().apply {
                putString("project_id", projectId)
            }
            analytics.logEvent("project_view", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logProjectView error: ${e.message}")
        }
    }

    /**
     * Triggered when a support ticket is created in client portal.
     */
    fun logSupportTicketCreated(projectId: String, priority: String = "") {
        try {
            val bundle = Bundle().apply {
                putString("project_id", projectId)
                if (priority.isNotBlank()) putString("priority", priority)
            }
            analytics.logEvent("support_ticket_created", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logSupportTicketCreated error: ${e.message}")
        }
    }

    fun logContactSubmitted(subject: String) {
        try {
            val bundle = Bundle().apply {
                putString("subject", subject)
            }
            analytics.logEvent("contact_inquiry_submitted", bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logContactSubmitted error: ${e.message}")
        }
    }

    fun logScreenView(screenName: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "logScreenView error: ${e.message}")
        }
    }

    fun setUserId(userId: String?) {
        try {
            analytics.setUserId(userId)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "setUserId error: ${e.message}")
        }
    }

    fun setUserRole(role: String) {
        try {
            analytics.setUserProperty("user_role", role)
        } catch (e: Exception) {
            Log.w("AnalyticsHelper", "setUserRole error: ${e.message}")
        }
    }
}

