package com.gotechmedia.app.presentation.screens.quote

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.data.firebase.FirebaseAnalyticsHelper
import com.gotechmedia.app.domain.model.AgencyLead
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.LeadAttachment
import com.gotechmedia.app.domain.model.LeadStatus
import com.gotechmedia.app.domain.model.QuoteFieldErrors
import com.gotechmedia.app.domain.model.QuoteStep
import com.gotechmedia.app.domain.repository.AuthRepository
import com.gotechmedia.app.domain.usecase.GetAgencyServicesUseCase
import com.gotechmedia.app.domain.usecase.firestore.QuoteSubmissionParams
import com.gotechmedia.app.domain.usecase.firestore.SubmitQuoteLeadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class QuoteErrorType {
    VALIDATION,
    NETWORK_OFFLINE,
    STORAGE_UPLOAD,
    FIRESTORE,
    DUPLICATE
}

sealed interface QuoteSubmissionState {
    data object Idle : QuoteSubmissionState
    data class Submitting(val stepProgress: String) : QuoteSubmissionState
    data class Success(val referenceId: String, val lead: AgencyLead) : QuoteSubmissionState
    data class Error(val errorType: QuoteErrorType, val userFriendlyMessage: String) : QuoteSubmissionState
}

class CustomQuoteViewModel(
    initialServiceId: String = "",
    private val getAgencyServicesUseCase: GetAgencyServicesUseCase,
    private val submitQuoteLeadUseCase: SubmitQuoteLeadUseCase,
    private val authRepository: AuthRepository,
    private val analyticsHelper: FirebaseAnalyticsHelper
) : ViewModel() {

    // Available Services
    private val _availableServices = MutableStateFlow<List<AgencyService>>(emptyList())
    val availableServices: StateFlow<List<AgencyService>> = _availableServices.asStateFlow()

    // Stepper State
    private val _currentStep = MutableStateFlow(QuoteStep.SERVICE_SELECTION)
    val currentStep: StateFlow<QuoteStep> = _currentStep.asStateFlow()

    // Form Inputs
    private val _selectedServiceIds = MutableStateFlow<Set<String>>(
        if (initialServiceId.isNotBlank()) setOf(initialServiceId) else emptySet()
    )
    val selectedServiceIds: StateFlow<Set<String>> = _selectedServiceIds.asStateFlow()

    private val _projectDescription = MutableStateFlow("")
    val projectDescription: StateFlow<String> = _projectDescription.asStateFlow()

    private val _budgetRange = MutableStateFlow("$25k – $50k (Growth Platform)")
    val budgetRange: StateFlow<String> = _budgetRange.asStateFlow()

    private val _expectedTimeline = MutableStateFlow("1 – 3 Months")
    val expectedTimeline: StateFlow<String> = _expectedTimeline.asStateFlow()

    private val _referenceWebsite = MutableStateFlow("")
    val referenceWebsite: StateFlow<String> = _referenceWebsite.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _company = MutableStateFlow("")
    val company: StateFlow<String> = _company.asStateFlow()

    private val _attachment = MutableStateFlow<LeadAttachment?>(null)
    val attachment: StateFlow<LeadAttachment?> = _attachment.asStateFlow()

    // Field-level validation errors
    private val _fieldErrors = MutableStateFlow(QuoteFieldErrors())
    val fieldErrors: StateFlow<QuoteFieldErrors> = _fieldErrors.asStateFlow()

    // Overall submission lifecycle
    private val _submissionState = MutableStateFlow<QuoteSubmissionState>(QuoteSubmissionState.Idle)
    val submissionState: StateFlow<QuoteSubmissionState> = _submissionState.asStateFlow()

    // Duplicate submission cache
    private var lastSubmittedSignature: String? = null
    private var lastSubmittedReferenceId: String? = null

    init {
        // Track quote started in Firebase Analytics
        analyticsHelper.logQuoteStarted("request_a_quote", initialServiceId)

        loadServices(initialServiceId)
        prefillAuthenticatedUserData()
    }

    private fun loadServices(initialServiceId: String) {
        getAgencyServicesUseCase().onEach { res ->
            if (res is Resource.Success) {
                val list = res.data ?: emptyList()
                _availableServices.value = list
                if (initialServiceId.isNotBlank() && _selectedServiceIds.value.isEmpty()) {
                    _selectedServiceIds.value = setOf(initialServiceId)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun prefillAuthenticatedUserData() {
        val user = authRepository.currentUser
        if (user != null) {
            if (_fullName.value.isBlank() && user.displayName.isNotBlank()) {
                _fullName.value = user.displayName
            }
            if (_email.value.isBlank() && user.email.isNotBlank()) {
                _email.value = user.email
            }
            if (_phone.value.isBlank() && user.phone.isNotBlank()) {
                _phone.value = user.phone
            }
            if (_company.value.isBlank() && user.company.isNotBlank()) {
                _company.value = user.company
            }
        }
    }

    // Step 1: Services Mutators
    fun toggleService(serviceId: String) {
        val current = _selectedServiceIds.value.toMutableSet()
        if (current.contains(serviceId)) {
            current.remove(serviceId)
        } else {
            current.add(serviceId)
        }
        _selectedServiceIds.value = current
        if (current.isNotEmpty()) {
            _fieldErrors.value = _fieldErrors.value.copy(services = null)
        }
    }

    // Step 2: Requirements Mutators
    fun setProjectDescription(value: String) {
        _projectDescription.value = value
        if (value.trim().length >= 15) {
            _fieldErrors.value = _fieldErrors.value.copy(projectDescription = null)
        }
    }

    fun setBudgetRange(value: String) {
        _budgetRange.value = value
        _fieldErrors.value = _fieldErrors.value.copy(budgetRange = null)
    }

    fun setExpectedTimeline(value: String) {
        _expectedTimeline.value = value
        _fieldErrors.value = _fieldErrors.value.copy(expectedTimeline = null)
    }

    fun setReferenceWebsite(value: String) {
        _referenceWebsite.value = value
        _fieldErrors.value = _fieldErrors.value.copy(referenceWebsite = null)
    }

    // Step 3: Contact Info Mutators
    fun setFullName(value: String) {
        _fullName.value = value
        if (value.trim().length >= 2) {
            _fieldErrors.value = _fieldErrors.value.copy(fullName = null)
        }
    }

    fun setEmail(value: String) {
        _email.value = value
        if (value.contains("@") && value.contains(".")) {
            _fieldErrors.value = _fieldErrors.value.copy(email = null)
        }
    }

    fun setPhone(value: String) {
        _phone.value = value
        if (value.trim().length >= 7) {
            _fieldErrors.value = _fieldErrors.value.copy(phone = null)
        }
    }

    fun setCompany(value: String) {
        _company.value = value
        if (value.trim().length >= 2) {
            _fieldErrors.value = _fieldErrors.value.copy(company = null)
        }
    }

    // Step 4: Attachment Mutators
    fun setAttachment(uri: Uri, name: String, sizeBytes: Long, mimeType: String) {
        val attachmentObj = LeadAttachment(
            uri = uri,
            name = name,
            sizeBytes = sizeBytes,
            mimeType = mimeType
        )

        val error = when {
            !attachmentObj.isSizeValid -> "File exceeds 20 MB size limit (${attachmentObj.formattedSize})."
            !attachmentObj.isFormatSupported -> "Unsupported format. Please upload PDF, DOCX, image, or ZIP."
            else -> null
        }

        _attachment.value = attachmentObj
        _fieldErrors.value = _fieldErrors.value.copy(attachment = error)
    }

    fun removeAttachment() {
        _attachment.value = null
        _fieldErrors.value = _fieldErrors.value.copy(attachment = null)
    }

    // Stepper Navigation
    fun nextStep(): Boolean {
        val current = _currentStep.value
        val isValid = validateCurrentStep(current)
        if (isValid) {
            _currentStep.value = current.next()
            return true
        }
        return false
    }

    fun previousStep() {
        _currentStep.value = _currentStep.value.previous()
    }

    fun goToStep(step: QuoteStep) {
        _currentStep.value = step
    }

    private fun validateCurrentStep(step: QuoteStep): Boolean {
        when (step) {
            QuoteStep.SERVICE_SELECTION -> {
                if (_selectedServiceIds.value.isEmpty()) {
                    _fieldErrors.value = _fieldErrors.value.copy(
                        services = "Please select at least one service practice to continue."
                    )
                    return false
                }
            }
            QuoteStep.REQUIREMENT_DETAILS -> {
                var hasError = false
                val desc = _projectDescription.value.trim()
                val descErr = if (desc.isEmpty()) {
                    hasError = true
                    "Project Description is required."
                } else if (desc.length < 15) {
                    hasError = true
                    "Please provide a more detailed project brief (min 15 characters)."
                } else null

                val budgetErr = if (_budgetRange.value.trim().isEmpty()) {
                    hasError = true
                    "Please select an estimated budget range."
                } else null

                val timelineErr = if (_expectedTimeline.value.trim().isEmpty()) {
                    hasError = true
                    "Please select an expected timeline."
                } else null

                val website = _referenceWebsite.value.trim()
                val websiteErr = if (website.isNotEmpty() && (!website.contains(".") || website.length < 4)) {
                    hasError = true
                    "Please enter a valid website URL."
                } else null

                _fieldErrors.value = _fieldErrors.value.copy(
                    projectDescription = descErr,
                    budgetRange = budgetErr,
                    expectedTimeline = timelineErr,
                    referenceWebsite = websiteErr
                )
                if (hasError) return false
            }
            QuoteStep.CONTACT_INFO -> {
                var hasError = false
                val name = _fullName.value.trim()
                val nameErr = if (name.isEmpty()) {
                    hasError = true
                    "Full Name is required."
                } else if (name.length < 2) {
                    hasError = true
                    "Name must be at least 2 characters."
                } else null

                val email = _email.value.trim()
                val emailErr = if (email.isEmpty()) {
                    hasError = true
                    "Work Email is required."
                } else if (!email.contains("@") || !email.contains(".")) {
                    hasError = true
                    "Please enter a valid work email."
                } else null

                val phone = _phone.value.trim()
                val phoneErr = if (phone.isEmpty()) {
                    hasError = true
                    "Phone / WhatsApp number is required."
                } else if (phone.length < 7) {
                    hasError = true
                    "Please enter a valid phone number (min 7 digits)."
                } else null

                val company = _company.value.trim()
                val companyErr = if (company.isEmpty()) {
                    hasError = true
                    "Company or Business Name is required."
                } else if (company.length < 2) {
                    hasError = true
                    "Company name must be at least 2 characters."
                } else null

                _fieldErrors.value = _fieldErrors.value.copy(
                    fullName = nameErr,
                    email = emailErr,
                    phone = phoneErr,
                    company = companyErr
                )
                if (hasError) return false
            }
            QuoteStep.ATTACHMENT -> {
                val att = _attachment.value
                if (att != null) {
                    if (!att.isSizeValid || !att.isFormatSupported) {
                        return false
                    }
                }
            }
            QuoteStep.REVIEW -> {
                // All steps are verified in full validation on submit
            }
        }
        return true
    }

    // Submission Pipeline
    fun submitQuote() {
        if (_submissionState.value is QuoteSubmissionState.Submitting) return

        val params = QuoteSubmissionParams(
            fullName = _fullName.value,
            email = _email.value,
            phone = _phone.value,
            company = _company.value,
            selectedServiceIds = _selectedServiceIds.value.toList(),
            primaryServiceTitle = getPrimaryServiceTitle(),
            projectDescription = _projectDescription.value,
            budgetRange = _budgetRange.value,
            expectedTimeline = _expectedTimeline.value,
            referenceWebsite = _referenceWebsite.value,
            attachment = _attachment.value
        )

        // 1. Validation check
        val validationErrors = submitQuoteLeadUseCase.validateParams(params)
        if (validationErrors.hasErrors) {
            _fieldErrors.value = validationErrors
            _submissionState.value = QuoteSubmissionState.Error(
                errorType = QuoteErrorType.VALIDATION,
                userFriendlyMessage = "Please resolve the highlighted requirements before submitting."
            )
            return
        }

        // 2. Prevent obvious duplicate submissions
        val currentPayloadSignature = "${params.fullName.trim()}|${params.email.trim()}|${params.company.trim()}|${params.selectedServiceIds.sorted().joinToString()}|${params.projectDescription.trim()}"
        if (currentPayloadSignature == lastSubmittedSignature && lastSubmittedReferenceId != null) {
            _submissionState.value = QuoteSubmissionState.Error(
                errorType = QuoteErrorType.DUPLICATE,
                userFriendlyMessage = "This quote request has already been received (Reference: $lastSubmittedReferenceId). Our engineering leads are reviewing your brief."
            )
            return
        }

        viewModelScope.launch {
            _submissionState.value = QuoteSubmissionState.Submitting("Validating project scope...")

            submitQuoteLeadUseCase(params).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _submissionState.value = QuoteSubmissionState.Submitting(
                            if (params.attachment != null) "Uploading project specifications & synchronizing brief..."
                            else "Registering custom quote inquiry in enterprise repository..."
                        )
                    }
                    is Resource.Success -> {
                        val refId = resource.data ?: "GTM-QUOTE-REC"
                        lastSubmittedSignature = currentPayloadSignature
                        lastSubmittedReferenceId = refId

                        // Log Firebase Analytics Event
                        analyticsHelper.logQuoteSubmitted(
                            leadId = refId,
                            serviceCount = params.selectedServiceIds.size,
                            primaryService = params.primaryServiceTitle,
                            budgetTier = params.budgetRange,
                            timeline = params.expectedTimeline,
                            hasAttachment = params.attachment != null
                        )

                        val leadSummary = AgencyLead(
                            id = refId,
                            leadReferenceId = refId,
                            clientName = params.fullName.trim(),
                            email = params.email.trim(),
                            company = params.company.trim(),
                            phone = params.phone.trim(),
                            serviceRequested = params.primaryServiceTitle,
                            selectedServices = params.selectedServiceIds,
                            projectDescription = params.projectDescription.trim(),
                            budgetRange = params.budgetRange,
                            expectedTimeline = params.expectedTimeline,
                            referenceWebsite = params.referenceWebsite.trim(),
                            attachmentName = params.attachment?.name,
                            attachmentSizeBytes = params.attachment?.sizeBytes ?: 0L,
                            source = "REQUEST_A_QUOTE",
                            status = LeadStatus.NEW.displayName,
                            createdAtEpoch = System.currentTimeMillis()
                        )

                        _submissionState.value = QuoteSubmissionState.Success(
                            referenceId = refId,
                            lead = leadSummary
                        )
                    }
                    is Resource.Error -> {
                        val errMessage = resource.message
                        val errorType = when {
                            errMessage.contains("network", ignoreCase = true) ||
                            errMessage.contains("offline", ignoreCase = true) ||
                            errMessage.contains("unavailable", ignoreCase = true) ->
                                QuoteErrorType.NETWORK_OFFLINE
                            errMessage.contains("attachment", ignoreCase = true) ||
                            errMessage.contains("upload", ignoreCase = true) ->
                                QuoteErrorType.STORAGE_UPLOAD
                            else ->
                                QuoteErrorType.FIRESTORE
                        }
                        _submissionState.value = QuoteSubmissionState.Error(
                            errorType = errorType,
                            userFriendlyMessage = errMessage
                        )
                    }
                }
            }
        }
    }

    private fun getPrimaryServiceTitle(): String {
        val selectedIds = _selectedServiceIds.value
        val servicesList = _availableServices.value
        val primary = servicesList.find { selectedIds.contains(it.id) }
        return primary?.title ?: (selectedIds.firstOrNull() ?: "Custom Architecture")
    }

    fun dismissError() {
        if (_submissionState.value is QuoteSubmissionState.Error) {
            _submissionState.value = QuoteSubmissionState.Idle
        }
    }

    fun reset() {
        _currentStep.value = QuoteStep.SERVICE_SELECTION
        _submissionState.value = QuoteSubmissionState.Idle
        _projectDescription.value = ""
        _referenceWebsite.value = ""
        _attachment.value = null
        _fieldErrors.value = QuoteFieldErrors()
        // Retain user contact info for convenience, clear service selections
        _selectedServiceIds.value = emptySet()
    }
}
