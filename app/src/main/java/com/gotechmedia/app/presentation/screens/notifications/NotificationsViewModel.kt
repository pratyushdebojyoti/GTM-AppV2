package com.gotechmedia.app.presentation.screens.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.notification.FcmTokenManager
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.NotificationType
import com.gotechmedia.app.domain.repository.AuthRepository
import com.gotechmedia.app.domain.usecase.notification.DeleteNotificationUseCase
import com.gotechmedia.app.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.gotechmedia.app.domain.usecase.notification.MarkNotificationAsReadUseCase
import com.gotechmedia.app.domain.usecase.notification.ObserveUserNotificationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NotificationFilter(val displayName: String) {
    ALL("All"),
    UNREAD("Unread"),
    PROJECTS("Projects"),
    MILESTONES("Milestones"),
    MESSAGES("Messages"),
    SUPPORT("Support"),
    ANNOUNCEMENTS("Announcements"),
    LEADS("Leads")
}

data class NotificationsUiState(
    val notifications: List<AgencyNotification> = emptyList(),
    val filteredNotifications: List<AgencyNotification> = emptyList(),
    val unreadCount: Int = 0,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPushPermissionGranted: Boolean = true,
    val deviceToken: String? = null,
    val isTokenSyncing: Boolean = false,
    val currentUserId: String? = null
)

class NotificationsViewModel(
    private val observeUserNotificationsUseCase: ObserveUserNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val authRepository: AuthRepository,
    private val appContext: Context
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<AgencyNotification>>(emptyList())
    private val _selectedFilter = MutableStateFlow(NotificationFilter.ALL)
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isPermissionGranted = MutableStateFlow(FcmTokenManager.checkPermission(appContext))
    private val _currentUserId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NotificationsUiState> = combine(
        _notifications,
        _selectedFilter,
        _isLoading,
        _errorMessage,
        _isPermissionGranted,
        FcmTokenManager.deviceToken,
        FcmTokenManager.isTokenSyncing,
        _currentUserId
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val rawNotifications = params[0] as List<AgencyNotification>
        val filter = params[1] as NotificationFilter
        val loading = params[2] as Boolean
        val error = params[3] as? String
        val permission = params[4] as Boolean
        val token = params[5] as? String
        val syncing = params[6] as Boolean
        val userId = params[7] as? String

        val unread = rawNotifications.count { !it.read }

        val filtered = when (filter) {
            NotificationFilter.ALL -> rawNotifications
            NotificationFilter.UNREAD -> rawNotifications.filter { !it.read }
            NotificationFilter.PROJECTS -> rawNotifications.filter { it.type == NotificationType.PROJECT_UPDATE.code }
            NotificationFilter.MILESTONES -> rawNotifications.filter { it.type == NotificationType.MILESTONE_UPDATE.code }
            NotificationFilter.MESSAGES -> rawNotifications.filter { it.type == NotificationType.NEW_MESSAGE.code }
            NotificationFilter.SUPPORT -> rawNotifications.filter { it.type == NotificationType.SUPPORT_TICKET_UPDATE.code }
            NotificationFilter.ANNOUNCEMENTS -> rawNotifications.filter { it.type == NotificationType.GENERAL_ANNOUNCEMENT.code }
            NotificationFilter.LEADS -> rawNotifications.filter { it.type == NotificationType.NEW_LEAD.code }
        }

        NotificationsUiState(
            notifications = rawNotifications,
            filteredNotifications = filtered,
            unreadCount = unread,
            selectedFilter = filter,
            isLoading = loading,
            errorMessage = error,
            isPushPermissionGranted = permission,
            deviceToken = token ?: FcmTokenManager.getCachedToken(appContext),
            isTokenSyncing = syncing,
            currentUserId = userId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationsUiState(isLoading = true)
    )

    init {
        checkPermissionState()
        observeCurrentUserAndNotifications()
    }

    fun checkPermissionState() {
        _isPermissionGranted.value = FcmTokenManager.checkPermission(appContext)
    }

    fun onPermissionResult(granted: Boolean) {
        _isPermissionGranted.value = granted
        if (granted) {
            // Trigger device sync upon permission grant
            val userId = _currentUserId.value
            FcmTokenManager.syncDeviceToken(
                context = appContext,
                notificationRepository = com.gotechmedia.app.data.firebase.NotificationRepositoryImpl(),
                userId = userId,
                scope = viewModelScope
            )
        }
    }

    private fun observeCurrentUserAndNotifications() {
        viewModelScope.launch {
            authRepository.authState.collectLatest { state ->
                val uid = (state as? AuthState.Authenticated)?.user?.id.orEmpty()
                _currentUserId.value = uid
                subscribeToNotifications(uid)
            }
        }
    }

    private fun subscribeToNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            observeUserNotificationsUseCase(userId).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _errorMessage.value = null
                        _notifications.value = resource.data
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = resource.message
                    }
                }
            }
        }
    }

    fun setFilter(filter: NotificationFilter) {
        _selectedFilter.value = filter
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            // Optimistic update
            _notifications.value = _notifications.value.map {
                if (it.id == notificationId) it.copy(read = true) else it
            }
            markNotificationAsReadUseCase(notificationId).collectLatest { }
        }
    }

    fun markAllAsRead() {
        val uid = _currentUserId.value.orEmpty()
        viewModelScope.launch {
            // Optimistic update
            _notifications.value = _notifications.value.map { it.copy(read = true) }
            markAllNotificationsAsReadUseCase(uid).collectLatest { }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            // Optimistic update
            _notifications.value = _notifications.value.filter { it.id != notificationId }
            deleteNotificationUseCase(notificationId).collectLatest { }
        }
    }

    fun refresh() {
        subscribeToNotifications(_currentUserId.value.orEmpty())
    }
}
