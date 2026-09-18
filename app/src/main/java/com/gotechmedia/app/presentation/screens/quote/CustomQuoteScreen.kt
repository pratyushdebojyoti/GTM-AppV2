package com.gotechmedia.app.presentation.screens.quote

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.domain.model.AgencyLead
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.LeadAttachment
import com.gotechmedia.app.domain.model.QuoteFieldErrors
import com.gotechmedia.app.domain.model.QuoteStep
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.ObsidianSurfaceVariant
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun CustomQuoteScreen(
    viewModel: CustomQuoteViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val services by viewModel.availableServices.collectAsState()
    val selectedServiceIds by viewModel.selectedServiceIds.collectAsState()

    val projectDescription by viewModel.projectDescription.collectAsState()
    val budgetRange by viewModel.budgetRange.collectAsState()
    val expectedTimeline by viewModel.expectedTimeline.collectAsState()
    val referenceWebsite by viewModel.referenceWebsite.collectAsState()

    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val company by viewModel.company.collectAsState()

    val attachment by viewModel.attachment.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val contentResolver = context.contentResolver
            var fileName = "project_specification"
            var fileSize = 0L
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: "project_specification"
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }
            viewModel.setAttachment(uri, fileName, fileSize, mimeType)
        }
    }

    LaunchedEffect(submissionState) {
        if (submissionState is QuoteSubmissionState.Error) {
            val msg = (submissionState as QuoteSubmissionState.Error).userFriendlyMessage
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Request a Quote",
                subtitle = "Step ${currentStep.stepNumber} of 5: ${currentStep.title}",
                onBackClick = {
                    if (submissionState is QuoteSubmissionState.Success) {
                        onNavigateBack()
                    } else if (currentStep.isFirst) {
                        onNavigateBack()
                    } else {
                        viewModel.previousStep()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("request_a_quote_screen")
    ) { innerPadding ->
        if (submissionState is QuoteSubmissionState.Success) {
            val successState = submissionState as QuoteSubmissionState.Success
            QuoteSuccessView(
                referenceId = successState.referenceId,
                lead = successState.lead,
                onCopyReference = { ref ->
                    clipboardManager.setText(AnnotatedString(ref))
                    scope.launch {
                        snackbarHostState.showSnackbar("Reference code copied to clipboard!")
                    }
                },
                onSubmitAnother = { viewModel.reset() },
                onReturnHome = onNavigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                QuoteStepProgressBar(
                    currentStep = currentStep,
                    onStepClick = { step ->
                        if (step.stepNumber < currentStep.stepNumber) {
                            viewModel.goToStep(step)
                        }
                    }
                )

                AnimatedVisibility(visible = submissionState is QuoteSubmissionState.Submitting) {
                    val progressMsg = (submissionState as? QuoteSubmissionState.Submitting)?.stepProgress
                        ?: "Synchronizing project brief..."
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianSurface)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ElectricCyan,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = progressMsg,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = submissionState is QuoteSubmissionState.Error) {
                    val err = submissionState as? QuoteSubmissionState.Error
                    if (err != null) {
                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = err.userFriendlyMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.dismissError() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "QuoteSteps"
                    ) { step ->
                        when (step) {
                            QuoteStep.SERVICE_SELECTION -> {
                                ServiceSelectionStepView(
                                    services = services,
                                    selectedIds = selectedServiceIds,
                                    error = fieldErrors.services,
                                    onToggleService = { viewModel.toggleService(it) }
                                )
                            }
                            QuoteStep.REQUIREMENT_DETAILS -> {
                                RequirementDetailsStepView(
                                    projectDescription = projectDescription,
                                    onDescriptionChange = { viewModel.setProjectDescription(it) },
                                    budgetRange = budgetRange,
                                    onBudgetChange = { viewModel.setBudgetRange(it) },
                                    expectedTimeline = expectedTimeline,
                                    onTimelineChange = { viewModel.setExpectedTimeline(it) },
                                    referenceWebsite = referenceWebsite,
                                    onWebsiteChange = { viewModel.setReferenceWebsite(it) },
                                    errors = fieldErrors
                                )
                            }
                            QuoteStep.CONTACT_INFO -> {
                                ContactInfoStepView(
                                    fullName = fullName,
                                    onFullNameChange = { viewModel.setFullName(it) },
                                    email = email,
                                    onEmailChange = { viewModel.setEmail(it) },
                                    phone = phone,
                                    onPhoneChange = { viewModel.setPhone(it) },
                                    company = company,
                                    onCompanyChange = { viewModel.setCompany(it) },
                                    errors = fieldErrors
                                )
                            }
                            QuoteStep.ATTACHMENT -> {
                                AttachmentStepView(
                                    attachment = attachment,
                                    onPickFile = { filePickerLauncher.launch("*/*") },
                                    onRemove = { viewModel.removeAttachment() },
                                    error = fieldErrors.attachment
                                )
                            }
                            QuoteStep.REVIEW -> {
                                ReviewStepView(
                                    services = services.filter { selectedServiceIds.contains(it.id) },
                                    projectDescription = projectDescription,
                                    budgetRange = budgetRange,
                                    expectedTimeline = expectedTimeline,
                                    referenceWebsite = referenceWebsite,
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    company = company,
                                    attachment = attachment,
                                    onEditStep = { targetStep -> viewModel.goToStep(targetStep) }
                                )
                            }
                        }
                    }
                }

                QuoteBottomActionBar(
                    currentStep = currentStep,
                    isSubmitting = submissionState is QuoteSubmissionState.Submitting,
                    onPrevious = { viewModel.previousStep() },
                    onNext = { viewModel.nextStep() },
                    onSubmit = { viewModel.submitQuote() }
                )
            }
        }
    }
}

// 1. Progress Bar
@Composable
private fun QuoteStepProgressBar(
    currentStep: QuoteStep,
    onStepClick: (QuoteStep) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = currentStep.stepNumber / 5f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuoteStep.entries.forEach { step ->
                val isActive = step == currentStep
                val isCompleted = step.stepNumber < currentStep.stepNumber

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = isCompleted) {
                        onStepClick(step)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> ElectricCyan
                                    isActive -> ElectricCyan.copy(alpha = 0.2f)
                                    else -> ObsidianSurfaceVariant
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isActive || isCompleted) ElectricCyan else GlassBorderSubtle,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ObsidianCanvas,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = step.stepNumber.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) ElectricCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = ElectricCyan,
            trackColor = ObsidianSurfaceVariant
        )
    }
}

// 2. Step 1: Service Selection
@Composable
private fun ServiceSelectionStepView(
    services: List<AgencyService>,
    selectedIds: Set<String>,
    error: String?,
    onToggleService: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Select Required Disciplines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose one or multiple practice areas required for your technical roadmap.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        if (error != null) {
            item {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        items(services.size) { index ->
            val service = services[index]
            val isSelected = selectedIds.contains(service.id)

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleService(service.id) }
                    .testTag("service_item_${service.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) ElectricCyan.copy(alpha = 0.08f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) ElectricCyan else GlassBorderSubtle,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) ElectricCyan else ObsidianSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ElectricCyan else GlassBorderSubtle,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ObsidianCanvas,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) ElectricCyan else Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = service.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// 3. Step 2: Requirement Details
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RequirementDetailsStepView(
    projectDescription: String,
    onDescriptionChange: (String) -> Unit,
    budgetRange: String,
    onBudgetChange: (String) -> Unit,
    expectedTimeline: String,
    onTimelineChange: (String) -> Unit,
    referenceWebsite: String,
    onWebsiteChange: (String) -> Unit,
    errors: QuoteFieldErrors
) {
    val budgetTiers = listOf(
        "$10k – $25k (MVP Launch)",
        "$25k – $50k (Growth Platform)",
        "$50k – $100k (Enterprise Scale)",
        "$100k+ (Strategic Transformation)",
        "Custom / Scoped on Brief"
    )

    val timelines = listOf(
        "Urgent (< 1 Month)",
        "1 – 3 Months",
        "3 – 6 Months",
        "6+ Months / Retainer"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Project Scope & Requirements",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Define your objectives, budget tier, and delivery timeline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        item {
            Column {
                Text(
                    text = "Project Description *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = projectDescription,
                    onValueChange = onDescriptionChange,
                    placeholder = {
                        Text(
                            "Detail your technical requirements, goals, target platforms, and integrations...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    isError = errors.projectDescription != null,
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_description_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GlassBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = ObsidianSurfaceVariant,
                        unfocusedContainerColor = ObsidianSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errors.projectDescription != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errors.projectDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5252)
                    )
                }
            }
        }

        item {
            Column {
                Text(
                    text = "Estimated Budget Tier *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    budgetTiers.forEach { tier ->
                        val isSelected = tier == budgetRange
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElectricCyan.copy(alpha = 0.15f) else ObsidianSurfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElectricCyan else GlassBorderSubtle,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onBudgetChange(tier) }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = tier,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) ElectricCyan else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    text = "Expected Timeline *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timelines.forEach { time ->
                        val isSelected = time == expectedTimeline
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElectricCyan.copy(alpha = 0.15f) else ObsidianSurfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElectricCyan else GlassBorderSubtle,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onTimelineChange(time) }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) ElectricCyan else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    text = "Reference Website or Benchmark (Optional)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = referenceWebsite,
                    onValueChange = onWebsiteChange,
                    placeholder = { Text("https://example.com or benchmark reference", color = TextMuted, fontSize = 14.sp) },
                    isError = errors.referenceWebsite != null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reference_website_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GlassBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = ObsidianSurfaceVariant,
                        unfocusedContainerColor = ObsidianSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                if (errors.referenceWebsite != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errors.referenceWebsite,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5252)
                    )
                }
            }
        }
    }
}

// 4. Step 3: Contact Info
@Composable
private fun ContactInfoStepView(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    company: String,
    onCompanyChange: (String) -> Unit,
    errors: QuoteFieldErrors
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Enterprise Contact Coordinates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Provide your business details so our engineering directors can reach you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        item {
            QuoteInputField(
                label = "Full Name *",
                value = fullName,
                onValueChange = onFullNameChange,
                placeholder = "Alex Mercer",
                error = errors.fullName,
                testTag = "full_name_input"
            )
        }

        item {
            QuoteInputField(
                label = "Work Email *",
                value = email,
                onValueChange = onEmailChange,
                placeholder = "alex@enterprise.com",
                error = errors.email,
                testTag = "email_input"
            )
        }

        item {
            QuoteInputField(
                label = "Phone / WhatsApp *",
                value = phone,
                onValueChange = onPhoneChange,
                placeholder = "+1 (555) 019-2834",
                error = errors.phone,
                testTag = "phone_input"
            )
        }

        item {
            QuoteInputField(
                label = "Company / Business Name *",
                value = company,
                onValueChange = onCompanyChange,
                placeholder = "Acme Technologies Corp",
                error = errors.company,
                testTag = "company_input"
            )
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = "Confidential",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Confidentiality guaranteed. All inquiries are bound by mutual bilateral non-disclosure protocols.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// 5. Step 4: Optional Attachment
@Composable
private fun AttachmentStepView(
    attachment: LeadAttachment?,
    onPickFile: () -> Unit,
    onRemove: () -> Unit,
    error: String?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Project Document / RFQ (Optional)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upload architecture diagrams, RFQs, PRDs, or wireframe archives (up to 20 MB).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        if (error != null) {
            item {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (attachment == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianSurfaceVariant)
                        .border(
                            width = 1.5.dp,
                            color = GlassBorderSubtle,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onPickFile() }
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = "Upload Document",
                                tint = ElectricCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = "Tap to browse project file",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        Text(
                            text = "Supports PDF, DOCX, PNG, JPG, or ZIP archives (Max 20 MB)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElectricCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attachment.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Size: ${attachment.formattedSize}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }

                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ObsidianSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onPickFile,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Replace File", color = ElectricCyan)
                }
            }
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Notice",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Files are securely stored in encrypted Firebase Cloud Storage buckets and accessible exclusively by assigned GoTech Solutions Architects.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// 6. Step 5: Review & Submit
@Composable
private fun ReviewStepView(
    services: List<AgencyService>,
    projectDescription: String,
    budgetRange: String,
    expectedTimeline: String,
    referenceWebsite: String,
    fullName: String,
    email: String,
    phone: String,
    company: String,
    attachment: LeadAttachment?,
    onEditStep: (QuoteStep) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Review Your Quote Request",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Verify your parameters before final dispatch to our solutions team.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        item {
            ReviewSectionCard(
                title = "Required Disciplines",
                onEdit = { onEditStep(QuoteStep.SERVICE_SELECTION) }
            ) {
                if (services.isEmpty()) {
                    Text("No services selected", color = Color(0xFFFF5252), fontSize = 13.sp)
                } else {
                    services.forEach { service ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        item {
            ReviewSectionCard(
                title = "Project Scope",
                onEdit = { onEditStep(QuoteStep.REQUIREMENT_DETAILS) }
            ) {
                ReviewDataRow(label = "Budget Tier", value = budgetRange)
                ReviewDataRow(label = "Timeline", value = expectedTimeline)
                if (referenceWebsite.isNotBlank()) {
                    ReviewDataRow(label = "Reference Site", value = referenceWebsite)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Project Brief:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = projectDescription.ifBlank { "None provided" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }

        item {
            ReviewSectionCard(
                title = "Contact Coordinates",
                onEdit = { onEditStep(QuoteStep.CONTACT_INFO) }
            ) {
                ReviewDataRow(label = "Full Name", value = fullName)
                ReviewDataRow(label = "Work Email", value = email)
                ReviewDataRow(label = "Phone / WhatsApp", value = phone)
                ReviewDataRow(label = "Company", value = company)
            }
        }

        item {
            ReviewSectionCard(
                title = "Specification File",
                onEdit = { onEditStep(QuoteStep.ATTACHMENT) }
            ) {
                if (attachment != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${attachment.name} (${attachment.formattedSize})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "No document attached (Optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

// 7. Bottom Action Bar
@Composable
private fun QuoteBottomActionBar(
    currentStep: QuoteStep,
    isSubmitting: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!currentStep.isFirst) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Back", color = Color.White)
                }
            }

            if (currentStep.isLast) {
                PrimaryCtaButton(
                    text = if (isSubmitting) "Submitting..." else "Submit Quote Request",
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .weight(if (currentStep.isFirst) 1f else 2f)
                        .testTag("submit_quote_button")
                )
            } else {
                PrimaryCtaButton(
                    text = if (currentStep == QuoteStep.ATTACHMENT) "Continue to Review" else "Continue",
                    onClick = onNext,
                    modifier = Modifier
                        .weight(if (currentStep.isFirst) 1f else 2f)
                        .testTag("continue_step_button")
                )
            }
        }
    }
}

// 8. Success Screen
@Composable
private fun QuoteSuccessView(
    referenceId: String,
    lead: AgencyLead,
    onCopyReference: (String) -> Unit,
    onSubmitAnother: () -> Unit,
    onReturnHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan.copy(alpha = 0.15f))
                    .border(1.5.dp, ElectricCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Success",
                    tint = ElectricCyan,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Quote Request Registered",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your requirements have been securely recorded in our enterprise lead registry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "UNIQUE LEAD REFERENCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = referenceId,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onCopyReference(referenceId) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Copy Reference", color = ElectricCyan, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Next Steps in Your Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    RoadmapStepItem(
                        number = "1",
                        title = "Engineering Triage (Within 24h)",
                        description = "Our Principal Architect reviews your technical scope and compatibility."
                    )
                    RoadmapStepItem(
                        number = "2",
                        title = "Discovery & Scoping Call",
                        description = "We schedule a 30-minute deep dive on architecture, stack, and deliverables."
                    )
                    RoadmapStepItem(
                        number = "3",
                        title = "Comprehensive Proposal & NDA",
                        description = "Receive detailed milestones, resource breakdown, and commercial terms."
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryCtaButton(
                    text = "Return to Home",
                    onClick = onReturnHome,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = onSubmitAnother,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Submit Another Quote", color = Color.White)
                }
            }
        }
    }
}

// 9. Reusable Input & Subcomponents
@Composable
private fun QuoteInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted, fontSize = 14.sp) },
            isError = error != null,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = GlassBorderSubtle,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = ObsidianSurfaceVariant,
                unfocusedContainerColor = ObsidianSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFF5252)
            )
        }
    }
}

@Composable
private fun ReviewSectionCard(
    title: String,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $title",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ReviewDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
private fun RoadmapStepItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ElectricCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
