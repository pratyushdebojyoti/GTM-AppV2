package com.gotechmedia.app.presentation.screens.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.usecase.auth.GetAuthStateUseCase
import com.gotechmedia.app.domain.usecase.auth.SignOutUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetClientProjectsUseCase
import com.gotechmedia.app.domain.usecase.firestore.MarkNotificationAsReadUseCase
import com.gotechmedia.app.domain.usecase.firestore.ObserveUserNotificationsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface ClientDashboardEvent {
    data object SignedOut : ClientDashboardEvent
    data class ShowToast(val message: String) : ClientDashboardEvent
}

class ClientDashboardViewModel(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getClientProjectsUseCase: GetClientProjectsUseCase,
    private val observeUserNotificationsUseCase: ObserveUserNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
) : ViewModel() {

    val authState: StateFlow<AuthState> = getAuthStateUseCase()

    private val _projectsState = MutableStateFlow<Resource<List<AgencyProject>>>(Resource.Loading)
    val projectsState: StateFlow<Resource<List<AgencyProject>>> = _projectsState.asStateFlow()

    private val _notificationsState = MutableStateFlow<Resource<List<AgencyNotification>>>(Resource.Loading)
    val notificationsState: StateFlow<Resource<List<AgencyNotification>>> = _notificationsState.asStateFlow()

    private val _events = MutableSharedFlow<ClientDashboardEvent>()
    val events: SharedFlow<ClientDashboardEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    loadDashboardData(state.user.id)
                } else {
                    _projectsState.value = Resource.Success(emptyList())
                    _notificationsState.value = Resource.Success(emptyList())
                }
            }
        }
    }

    fun loadDashboardData(userId: String) {
        getClientProjectsUseCase(userId).onEach { result ->
            _projectsState.value = result
        }.launchIn(viewModelScope)

        observeUserNotificationsUseCase(userId).onEach { result ->
            _notificationsState.value = result
        }.launchIn(viewModelScope)
    }

    fun markNotificationRead(notificationId: String) {
        markNotificationAsReadUseCase(notificationId).launchIn(viewModelScope)
    }

    fun signOut() {
        signOutUseCase().onEach { result ->
            if (result is Resource.Success) {
                _events.emit(ClientDashboardEvent.SignedOut)
            }
        }.launchIn(viewModelScope)
    }
}
