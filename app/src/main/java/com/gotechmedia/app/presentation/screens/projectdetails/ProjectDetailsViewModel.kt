package com.gotechmedia.app.presentation.screens.projectdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyDocument
import com.gotechmedia.app.domain.model.AgencyMessage
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.AgencyProjectMilestone
import com.gotechmedia.app.domain.model.AgencySupportTicket
import com.gotechmedia.app.domain.model.TicketPriority
import com.gotechmedia.app.domain.model.TicketStatus
import com.gotechmedia.app.domain.usecase.auth.GetAuthStateUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetClientTicketsUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetProjectDetailsUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetProjectDocumentsUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetProjectMessagesUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetProjectMilestonesUseCase
import com.gotechmedia.app.domain.usecase.firestore.SendProjectMessageUseCase
import com.gotechmedia.app.domain.usecase.firestore.SubmitSupportTicketUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface ProjectDetailsEvent {
    data class ShowToast(val message: String) : ProjectDetailsEvent
    data object TicketCreated : ProjectDetailsEvent
    data object MessageSent : ProjectDetailsEvent
}

class ProjectDetailsViewModel(
    val projectId: String,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val getProjectDetailsUseCase: GetProjectDetailsUseCase,
    private val getProjectMilestonesUseCase: GetProjectMilestonesUseCase,
    private val getProjectDocumentsUseCase: GetProjectDocumentsUseCase,
    private val getProjectMessagesUseCase: GetProjectMessagesUseCase,
    private val sendProjectMessageUseCase: SendProjectMessageUseCase,
    private val getClientTicketsUseCase: GetClientTicketsUseCase,
    private val submitSupportTicketUseCase: SubmitSupportTicketUseCase
) : ViewModel() {

    val authState: StateFlow<AuthState> = getAuthStateUseCase()

    private val _projectState = MutableStateFlow<Resource<AgencyProject?>>(Resource.Loading)
    val projectState: StateFlow<Resource<AgencyProject?>> = _projectState.asStateFlow()

    private val _milestonesState = MutableStateFlow<Resource<List<AgencyProjectMilestone>>>(Resource.Loading)
    val milestonesState: StateFlow<Resource<List<AgencyProjectMilestone>>> = _milestonesState.asStateFlow()

    private val _documentsState = MutableStateFlow<Resource<List<AgencyDocument>>>(Resource.Loading)
    val documentsState: StateFlow<Resource<List<AgencyDocument>>> = _documentsState.asStateFlow()

    private val _messagesState = MutableStateFlow<Resource<List<AgencyMessage>>>(Resource.Loading)
    val messagesState: StateFlow<Resource<List<AgencyMessage>>> = _messagesState.asStateFlow()

    private val _ticketsState = MutableStateFlow<Resource<List<AgencySupportTicket>>>(Resource.Loading)
    val ticketsState: StateFlow<Resource<List<AgencySupportTicket>>> = _ticketsState.asStateFlow()

    private val _events = MutableSharedFlow<ProjectDetailsEvent>()
    val events: SharedFlow<ProjectDetailsEvent> = _events.asSharedFlow()

    init {
        loadAllProjectData()
    }

    fun loadAllProjectData() {
        getProjectDetailsUseCase(projectId).onEach { result ->
            _projectState.value = result
        }.launchIn(viewModelScope)

        getProjectMilestonesUseCase(projectId).onEach { result ->
            _milestonesState.value = result
        }.launchIn(viewModelScope)

        getProjectDocumentsUseCase(projectId).onEach { result ->
            _documentsState.value = result
        }.launchIn(viewModelScope)

        // Conversation ID is tied to project ID
        getProjectMessagesUseCase("conv_$projectId").onEach { result ->
            _messagesState.value = result
        }.launchIn(viewModelScope)

        val currentUser = getAuthStateUseCase.currentUser
        if (currentUser != null) {
            getClientTicketsUseCase(currentUser.id).onEach { result ->
                _ticketsState.value = result
            }.launchIn(viewModelScope)
        } else {
            _ticketsState.value = Resource.Success(emptyList())
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        val currentUser = getAuthStateUseCase.currentUser ?: return
        val msg = AgencyMessage(
            conversationId = "conv_$projectId",
            projectId = projectId,
            senderId = currentUser.id,
            senderName = currentUser.displayName.ifBlank { "Client" },
            senderRole = "CLIENT",
            content = content.trim(),
            timestampEpoch = System.currentTimeMillis()
        )
        sendProjectMessageUseCase(msg).onEach { result ->
            if (result is Resource.Success) {
                _events.emit(ProjectDetailsEvent.MessageSent)
            } else if (result is Resource.Error) {
                _events.emit(ProjectDetailsEvent.ShowToast("Message could not be sent: ${result.message}"))
            }
        }.launchIn(viewModelScope)
    }

    fun createSupportTicket(
        subject: String,
        description: String,
        priority: TicketPriority
    ) {
        if (subject.isBlank() || description.isBlank()) {
            viewModelScope.launch {
                _events.emit(ProjectDetailsEvent.ShowToast("Please provide both subject and description."))
            }
            return
        }
        val currentUser = getAuthStateUseCase.currentUser ?: return
        val ticket = AgencySupportTicket(
            clientId = currentUser.id,
            userId = currentUser.id,
            subject = subject.trim(),
            description = description.trim(),
            priority = priority.displayName,
            status = TicketStatus.OPEN.displayName,
            createdAtEpoch = System.currentTimeMillis(),
            updatedAtEpoch = System.currentTimeMillis()
        )

        submitSupportTicketUseCase(ticket).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _events.emit(ProjectDetailsEvent.ShowToast("Support Ticket #${result.data.take(6).uppercase()} Created."))
                    _events.emit(ProjectDetailsEvent.TicketCreated)
                    // Refresh tickets
                    getClientTicketsUseCase(currentUser.id).onEach { tResult ->
                        _ticketsState.value = tResult
                    }.launchIn(viewModelScope)
                }
                is Resource.Error -> {
                    _events.emit(ProjectDetailsEvent.ShowToast("Submission failed: ${result.message}"))
                }
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }
}
