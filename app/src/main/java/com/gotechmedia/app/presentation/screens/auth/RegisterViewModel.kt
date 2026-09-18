package com.gotechmedia.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.auth.UserRole
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.usecase.auth.GetAuthStateUseCase
import com.gotechmedia.app.domain.usecase.auth.SignInWithGoogleUseCase
import com.gotechmedia.app.domain.usecase.auth.SignUpWithEmailUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed interface RegisterEvent {
    data object RegisterSuccess : RegisterEvent
    data class ShowToast(val message: String) : RegisterEvent
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    val authState: StateFlow<AuthState> = getAuthStateUseCase()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterEvent>()
    val events: SharedFlow<RegisterEvent> = _events.asSharedFlow()

    fun signUp(
        fullName: String,
        email: String,
        company: String,
        password: String,
        confirmPassword: String,
        agreeToTerms: Boolean
    ) {
        if (fullName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your full name.")
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid work email.")
            return
        }
        if (company.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your company / enterprise name.")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match.")
            return
        }
        if (!agreeToTerms) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please accept the Master Services Agreement & Terms.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        signUpWithEmailUseCase(
            email = email,
            password = password,
            displayName = fullName,
            company = company,
            role = UserRole.CLIENT
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                    _events.emit(RegisterEvent.RegisterSuccess)
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
                    _events.emit(RegisterEvent.RegisterSuccess)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
