package com.gotechmedia.app.domain.model

import android.net.Uri
import java.util.Locale

/**
 * Metadata descriptor for an optional document or specification attached to a lead.
 */
data class LeadAttachment(
    val uri: Uri,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String
) {
    val formattedSize: String
        get() = when {
            sizeBytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", sizeBytes / (1024.0 * 1024.0))
            sizeBytes >= 1024 -> String.format(Locale.US, "%.1f KB", sizeBytes / 1024.0)
            else -> "$sizeBytes B"
        }

    val isSizeValid: Boolean
        get() = sizeBytes <= MAX_FILE_SIZE_BYTES

    val isFormatSupported: Boolean
        get() {
            val lower = name.lowercase()
            val disallowedExtensions = listOf(".exe", ".apk", ".bat", ".sh", ".cmd", ".vbs", ".msi")
            if (disallowedExtensions.any { lower.endsWith(it) }) {
                return false
            }
            return true
        }

    companion object {
        const val MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024L // 20 MB
    }
}

/**
 * Validation errors mapped to specific fields for real-time user feedback.
 */
data class QuoteFieldErrors(
    val services: String? = null,
    val projectDescription: String? = null,
    val budgetRange: String? = null,
    val expectedTimeline: String? = null,
    val referenceWebsite: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val company: String? = null,
    val attachment: String? = null
) {
    val hasErrors: Boolean
        get() = services != null ||
                projectDescription != null ||
                budgetRange != null ||
                expectedTimeline != null ||
                referenceWebsite != null ||
                fullName != null ||
                email != null ||
                phone != null ||
                company != null ||
                attachment != null
}

/**
 * Flow step in the multi-stage quote configuration journey.
 */
enum class QuoteStep(val stepNumber: Int, val title: String, val subtitle: String) {
    SERVICE_SELECTION(1, "Service Disciplines", "Select the engineering capabilities required"),
    REQUIREMENT_DETAILS(2, "Requirement Scope", "Define project goals, budget tier, and timeline"),
    CONTACT_INFO(3, "Enterprise Contact", "Provide your professional contact coordinates"),
    ATTACHMENT(4, "Optional Attachment", "Attach architectural briefs, RFQ, or PRD"),
    REVIEW(5, "Review & Submit", "Verify all parameters before official dispatch");

    val isFirst: Boolean get() = this == SERVICE_SELECTION
    val isLast: Boolean get() = this == REVIEW

    fun next(): QuoteStep = when (this) {
        SERVICE_SELECTION -> REQUIREMENT_DETAILS
        REQUIREMENT_DETAILS -> CONTACT_INFO
        CONTACT_INFO -> ATTACHMENT
        ATTACHMENT -> REVIEW
        REVIEW -> REVIEW
    }

    fun previous(): QuoteStep = when (this) {
        SERVICE_SELECTION -> SERVICE_SELECTION
        REQUIREMENT_DETAILS -> SERVICE_SELECTION
        CONTACT_INFO -> REQUIREMENT_DETAILS
        ATTACHMENT -> CONTACT_INFO
        REVIEW -> ATTACHMENT
    }
}
