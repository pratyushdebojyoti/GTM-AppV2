package com.gotechmedia.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.usecase.auth.GetAuthStateUseCase
import com.gotechmedia.app.domain.usecase.auth.SendPasswordResetUseCase
import com.gotechmedia.app.domain.usecase.auth.SignInWithEmailUseCase
import com.gotechmedia.app.domain.usecase.auth.SignInWithGoogleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface LoginEvent {
    data object LoginSuccess : LoginEvent
    data class ShowToast(val message: String) : LoginEvent
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resetEmailSent: Boolean = false
)

class LoginViewModel(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    val authState: StateFlow<AuthState> = getAuthStateUseCase()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid work email.")
            return
        }
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your password.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        signInWithEmailUseCase(email, password).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                    _events.emit(LoginEvent.LoginSuccess)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun signInWithGoogle(idToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        signInWithGoogleUseCase(idToken).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(LoginEvent.LoginSuccess)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(errorMessage = "Enter your work email above, then tap 'Forgot password?'.")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        sendPasswordResetUseCase(email).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, resetEmailSent = true, errorMessage = null)
                    _events.emit(LoginEvent.ShowToast("Password recovery email dispatched to $email"))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
