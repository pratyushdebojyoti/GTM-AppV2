package com.gotechmedia.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.AgencySupportTicket
import com.gotechmedia.app.domain.usecase.auth.GetAuthStateUseCase
import com.gotechmedia.app.domain.usecase.auth.SignOutUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetClientProjectsUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetClientTicketsUseCase
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

sealed interface ProfileEvent {
    data object SignedOut : ProfileEvent
    data class ShowToast(val message: String) : ProfileEvent
}

class ProfileViewModel(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getClientProjectsUseCase: GetClientProjectsUseCase,
    private val getClientTicketsUseCase: GetClientTicketsUseCase,
    private val submitSupportTicketUseCase: SubmitSupportTicketUseCase
) : ViewModel() {

    val authState: StateFlow<AuthState> = getAuthStateUseCase()

    private val _projectsState = MutableStateFlow<Resource<List<AgencyProject>>>(Resource.Loading)
    val projectsState: StateFlow<Resource<List<AgencyProject>>> = _projectsState.asStateFlow()

    private val _ticketsState = MutableStateFlow<Resource<List<AgencySupportTicket>>>(Resource.Loading)
    val ticketsState: StateFlow<Resource<List<AgencySupportTicket>>> = _ticketsState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        // When authState transitions to Authenticated, load projects and tickets
        viewModelScope.launch {
            authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    loadClientData(state.user.id)
                } else {
                    _projectsState.value = Resource.Success(emptyList())
                    _ticketsState.value = Resource.Success(emptyList())
                }
            }
        }
    }

    fun loadClientData(userId: String) {
        getClientProjectsUseCase(userId).onEach { result ->
            _projectsState.value = result
        }.launchIn(viewModelScope)

        getClientTicketsUseCase(userId).onEach { result ->
            _ticketsState.value = result
        }.launchIn(viewModelScope)
    }

    fun signOut() {
        signOutUseCase().onEach { result ->
            if (result is Resource.Success) {
                _events.emit(ProfileEvent.SignedOut)
            }
        }.launchIn(viewModelScope)
    }

    fun createSupportTicket(subject: String, description: String, priority: String = "HIGH") {
        val currentUser = getAuthStateUseCase.currentUser ?: return
        val ticket = AgencySupportTicket(
            clientId = currentUser.id,
            userId = currentUser.id,
            subject = subject,
            description = description,
            priority = priority,
            status = "OPEN"
        )
        submitSupportTicketUseCase(ticket).onEach { result ->
            if (result is Resource.Success) {
                _events.emit(ProfileEvent.ShowToast("Support ticket #${result.data.take(6).uppercase()} submitted."))
                loadClientData(currentUser.id)
            }
        }.launchIn(viewModelScope)
    }
}
