package com.gotechmedia.app.domain.usecase.firestore

import android.content.Context
import android.util.Patterns
import com.gotechmedia.app.core.utils.LocalNotificationHelper
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyActivityLog
import com.gotechmedia.app.domain.model.AgencyLead
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.LeadAttachment
import com.gotechmedia.app.domain.model.LeadStatus
import com.gotechmedia.app.domain.model.QuoteFieldErrors
import com.gotechmedia.app.domain.repository.AuthRepository
import com.gotechmedia.app.domain.repository.FirestoreRepository
import com.gotechmedia.app.domain.repository.NotificationRepository
import com.gotechmedia.app.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

/**
 * Parameters submitted through the Request a Quote pipeline.
 */
data class QuoteSubmissionParams(
    val fullName: String,
    val email: String,
    val phone: String,
    val company: String,
    val selectedServiceIds: List<String>,
    val primaryServiceTitle: String,
    val projectDescription: String,
    val budgetRange: String,
    val expectedTimeline: String,
    val referenceWebsite: String,
    val attachment: LeadAttachment?
)

/**
 * Enterprise Use Case orchestrating the full quote lead generation workflow:
 * 1. Validates all required fields & attachment size/format constraints.
 * 2. Uploads optional attachments to Firebase Storage.
 * 3. Creates the immutable lead record in Cloud Firestore with unique reference ID.
 * 4. Dispatches in-app notification & local notification alert.
 * 5. Logs audit activity in Cloud Firestore.
 */
class SubmitQuoteLeadUseCase(
    private val firestoreRepository: FirestoreRepository,
    private val storageRepository: StorageRepository,
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository,
    private val context: Context
) {

    /**
     * Validates fields without triggering submission.
     */
    fun validateParams(params: QuoteSubmissionParams): QuoteFieldErrors {
        val nameError = when {
            params.fullName.trim().isEmpty() -> "Full Name is required."
            params.fullName.trim().length < 2 -> "Name must be at least 2 characters."
            else -> null
        }

        val emailTrimmed = params.email.trim()
        val emailError = when {
            emailTrimmed.isEmpty() -> "Work Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches() -> "Please provide a valid email address."
            else -> null
        }

        val phoneTrimmed = params.phone.trim()
        val phoneError = when {
            phoneTrimmed.isEmpty() -> "Phone or WhatsApp number is required."
            phoneTrimmed.length < 7 -> "Please provide a valid phone or WhatsApp number."
            else -> null
        }

        val companyError = when {
            params.company.trim().isEmpty() -> "Company or Business Name is required."
            params.company.trim().length < 2 -> "Please enter a valid company name."
            else -> null
        }

        val servicesError = when {
            params.selectedServiceIds.isEmpty() -> "Please select at least one service discipline."
            else -> null
        }

        val descriptionTrimmed = params.projectDescription.trim()
        val descriptionError = when {
            descriptionTrimmed.isEmpty() -> "Project Description is required."
            descriptionTrimmed.length < 15 -> "Please describe your project in more detail (min 15 characters)."
            else -> null
        }

        val budgetError = when {
            params.budgetRange.trim().isEmpty() -> "Please choose an estimated budget range."
            else -> null
        }

        val timelineError = when {
            params.expectedTimeline.trim().isEmpty() -> "Please select your expected timeline."
            else -> null
        }

        val websiteTrimmed = params.referenceWebsite.trim()
        val websiteError = if (websiteTrimmed.isNotEmpty()) {
            if (!websiteTrimmed.contains(".") || websiteTrimmed.length < 4) {
                "Please enter a valid website URL (e.g. https://example.com)."
            } else null
        } else null

        val attachmentError = params.attachment?.let { file ->
            when {
                !file.isSizeValid -> "Attachment exceeds the 20 MB size limit."
                !file.isFormatSupported -> "Unsupported file format. Please upload PDF, DOCX, image, or ZIP."
                else -> null
            }
        }

        return QuoteFieldErrors(
            fullName = nameError,
            email = emailError,
            phone = phoneError,
            company = companyError,
            services = servicesError,
            projectDescription = descriptionError,
            budgetRange = budgetError,
            expectedTimeline = timelineError,
            referenceWebsite = websiteError,
            attachment = attachmentError
        )
    }

    operator fun invoke(params: QuoteSubmissionParams): Flow<Resource<String>> = flow {
        emit(Resource.Loading)

        // 1. Comprehensive Field Validation
        val errors = validateParams(params)
        if (errors.hasErrors) {
            val firstErrorMessage = errors.fullName
                ?: errors.email
                ?: errors.phone
                ?: errors.company
                ?: errors.services
                ?: errors.projectDescription
                ?: errors.budgetRange
                ?: errors.expectedTimeline
                ?: errors.referenceWebsite
                ?: errors.attachment
                ?: "Please check the highlighted fields."
            emit(Resource.Error(firstErrorMessage))
            return@flow
        }

        // 2. Generate Unique Memorable Lead Identifier
        val randomSuffix = UUID.randomUUID().toString().replace("-", "").take(6).uppercase()
        val uniqueLeadId = "GTM-QUOTE-$randomSuffix"

        // 3. Resolve Authenticated User if present
        val currentUserId = authRepository.currentUser?.id

        // 4. Upload Attachment to Firebase Storage if provided
        var attachmentDownloadUrl: String? = null
        if (params.attachment != null) {
            try {
                val sanitizedFileName = params.attachment.name.replace(" ", "_")
                val storagePath = "leads/attachments/${uniqueLeadId}_$sanitizedFileName"
                val uploadResult = storageRepository.uploadFile(storagePath, params.attachment.uri).first()

                when (uploadResult) {
                    is Resource.Success -> {
                        attachmentDownloadUrl = uploadResult.data
                    }
                    is Resource.Error -> {
                        emit(Resource.Error("Attachment upload failed. Please verify your internet connection or remove the file and retry."))
                        return@flow
                    }
                    is Resource.Loading -> Unit
                }
            } catch (e: Exception) {
                emit(Resource.Error("Attachment upload could not be completed. Please check your network connection."))
                return@flow
            }
        }

        // 5. Build Lead Domain Model
        val lead = AgencyLead(
            id = uniqueLeadId,
            leadReferenceId = uniqueLeadId,
            userId = currentUserId,
            clientName = params.fullName.trim(),
            email = params.email.trim(),
            company = params.company.trim(),
            phone = params.phone.trim(),
            serviceRequested = params.primaryServiceTitle.ifBlank {
                params.selectedServiceIds.joinToString(", ")
            },
            selectedServices = params.selectedServiceIds,
            projectDescription = params.projectDescription.trim(),
            budgetRange = params.budgetRange,
            expectedTimeline = params.expectedTimeline,
            referenceWebsite = params.referenceWebsite.trim(),
            attachmentUrl = attachmentDownloadUrl,
            attachmentName = params.attachment?.name,
            attachmentSizeBytes = params.attachment?.sizeBytes ?: 0L,
            source = "REQUEST_A_QUOTE",
            status = LeadStatus.NEW.displayName,
            createdAtEpoch = System.currentTimeMillis()
        )

        // 6. Submit to Cloud Firestore
        val submissionResult = firestoreRepository.submitLead(lead).first()
        when (submissionResult) {
            is Resource.Success -> {
                // 7. Trigger Available Notification Mechanisms

                // A. Local Device Notification
                LocalNotificationHelper.showQuoteReceivedNotification(
                    context = context,
                    leadReferenceId = uniqueLeadId,
                    companyName = params.company.trim()
                )

                // B. In-App Notification Feed in Cloud Firestore
                try {
                    val inAppNotice = AgencyNotification(
                        id = UUID.randomUUID().toString(),
                        recipientId = currentUserId ?: "ALL",
                        title = "Quote Request Confirmed ($uniqueLeadId)",
                        body = "Your custom quote request for ${params.primaryServiceTitle} was received. A Solutions Architect will review it within 24 hours.",
                        type = "QUOTE_RECEIVED",
                        read = false,
                        timestampEpoch = System.currentTimeMillis(),
                        deepLink = "route_custom_quote"
                    )
                    notificationRepository.createNotification(inAppNotice).first()
                } catch (_: Exception) {
                    // Non-blocking notification dispatch
                }

                // C. Log Immutable Audit Record in Cloud Firestore
                try {
                    val activity = AgencyActivityLog(
                        id = UUID.randomUUID().toString(),
                        userId = currentUserId ?: "guest",
                        actorName = params.fullName.ifBlank { "Guest Lead" },
                        action = "QUOTE_SUBMITTED",
                        entityType = "leads",
                        entityId = uniqueLeadId,
                        timestampEpoch = System.currentTimeMillis(),
                        metadata = mapOf(
                            "company" to params.company,
                            "serviceCount" to params.selectedServiceIds.size.toString()
                        )
                    )
                    firestoreRepository.logActivity(activity).first()
                } catch (_: Exception) {
                    // Non-blocking audit log
                }

                emit(Resource.Success(uniqueLeadId))
            }
            is Resource.Error -> {
                emit(Resource.Error(submissionResult.message))
            }
            is Resource.Loading -> Unit
        }
    }
}
