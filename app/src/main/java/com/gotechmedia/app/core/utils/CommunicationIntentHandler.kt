package com.gotechmedia.app.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.gotechmedia.app.data.firebase.FirebaseAnalyticsHelper
import java.net.URLEncoder

/**
 * Handles communication intents:
 * 1. WhatsApp with deep link and fallback
 * 2. Phone Dialer
 * 3. Email client with chooser and fallback
 * 4. Browser URL
 *
 * Gracefully handles missing apps and tracks analytics events without leaking PII.
 */
object CommunicationIntentHandler {

    /**
     * Launches WhatsApp using deep link:
     * whatsapp://send?phone=<phone>&text=<message>
     * with web api.whatsapp.com / https://wa.me fallback if WhatsApp app is not installed.
     */
    fun openWhatsApp(
        context: Context,
        phoneNumberDigits: String = Constants.AGENCY_WHATSAPP_PHONE_DIGITS,
        message: String = Constants.WHATSAPP_PREFILLED_MESSAGE,
        analyticsHelper: FirebaseAnalyticsHelper? = null,
        sourceScreen: String = "contact",
        onFailure: ((String) -> Unit)? = null
    ) {
        analyticsHelper?.logWhatsAppClicked(sourceScreen = sourceScreen)

        try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            // Preferred WhatsApp native deep link
            val appUri = Uri.parse("whatsapp://send?phone=$phoneNumberDigits&text=$encodedMessage")
            val nativeIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if WhatsApp can resolve this intent directly
            val packageManager = context.packageManager
            val canResolveApp = nativeIntent.resolveActivity(packageManager) != null

            if (canResolveApp) {
                context.startActivity(nativeIntent)
            } else {
                // Fallback to web universal link (opens in browser or WhatsApp if registered)
                val webUri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumberDigits&text=$encodedMessage")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (webIntent.resolveActivity(packageManager) != null) {
                    context.startActivity(webIntent)
                } else {
                    val errorMsg = "WhatsApp is not available on this device."
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    onFailure?.invoke(errorMsg)
                }
            }
        } catch (e: Exception) {
            val fallbackMsg = "Could not open WhatsApp: ${e.localizedMessage ?: "Unknown error"}"
            Toast.makeText(context, fallbackMsg, Toast.LENGTH_SHORT).show()
            onFailure?.invoke(fallbackMsg)
        }
    }

    /**
     * Initiates a phone call action using Intent.ACTION_DIAL (safe, doesn't require dangerous CALL_PHONE permission).
     */
    fun dialPhoneNumber(
        context: Context,
        phoneNumber: String = Constants.AGENCY_CALL_NUMBER,
        analyticsHelper: FirebaseAnalyticsHelper? = null,
        sourceScreen: String = "contact",
        onFailure: ((String) -> Unit)? = null
    ) {
        analyticsHelper?.logCallClicked(sourceScreen = sourceScreen)

        try {
            val dialUri = Uri.parse("tel:${phoneNumber.trim()}")
            val dialIntent = Intent(Intent.ACTION_DIAL, dialUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (dialIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(dialIntent)
            } else {
                val errorMsg = "Phone dialer is not available on this device."
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                onFailure?.invoke(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Unable to launch phone dialer."
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            onFailure?.invoke(errorMsg)
        }
    }

    /**
     * Opens an email client addressed to GoTech Media.
     * Uses ACTION_SENDTO with mailto: to target email apps only, with chooser fallback.
     */
    fun sendEmail(
        context: Context,
        recipientEmail: String = Constants.AGENCY_EMAIL,
        subject: String = "GoTech Media Project Consultation",
        body: String = "Hello GoTech Media team,\n\nI would like to inquire about your engineering and design capabilities.",
        analyticsHelper: FirebaseAnalyticsHelper? = null,
        sourceScreen: String = "contact",
        onFailure: ((String) -> Unit)? = null
    ) {
        analyticsHelper?.logEmailClicked(sourceScreen = sourceScreen)

        try {
            val encodedSubject = Uri.encode(subject)
            val encodedBody = Uri.encode(body)
            val mailtoUri = Uri.parse("mailto:$recipientEmail?subject=$encodedSubject&body=$encodedBody")

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = mailtoUri
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val chooser = Intent.createChooser(emailIntent, "Send Email via...").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (emailIntent.resolveActivity(context.packageManager) != null ||
                chooser.resolveActivity(context.packageManager) != null
            ) {
                context.startActivity(chooser)
            } else {
                val errorMsg = "No email application found on this device."
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                onFailure?.invoke(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Unable to open email client."
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            onFailure?.invoke(errorMsg)
        }
    }

    /**
     * Opens the official agency website in the external browser.
     */
    fun openWebsite(
        context: Context,
        url: String = Constants.AGENCY_WEBSITE_URL,
        analyticsHelper: FirebaseAnalyticsHelper? = null,
        sourceScreen: String = "contact",
        onFailure: ((String) -> Unit)? = null
    ) {
        analyticsHelper?.logWebsiteClicked(sourceScreen = sourceScreen)

        try {
            val webUri = Uri.parse(url)
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (webIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(webIntent)
            } else {
                val errorMsg = "No web browser found on this device."
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                onFailure?.invoke(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Unable to open website."
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            onFailure?.invoke(errorMsg)
        }
    }
}
