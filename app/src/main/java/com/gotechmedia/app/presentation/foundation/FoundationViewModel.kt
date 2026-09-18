package com.gotechmedia.app.presentation.foundation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.usecase.GetAgencyProfileUseCase
import com.gotechmedia.app.domain.usecase.GetAgencyServicesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

/**
 * Foundation Architecture ViewModel.
 * Demonstrates MVVM pattern consuming domain use cases and exposing an immutable StateFlow.
 */
class FoundationViewModel(
    private val getAgencyProfileUseCase: GetAgencyProfileUseCase,
    private val getAgencyServicesUseCase: GetAgencyServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoundationUiState(isLoading = true))
    val uiState: StateFlow<FoundationUiState> = _uiState.asStateFlow()

    init {
        loadFoundationData()
    }

    fun loadFoundationData() {
        combine(
            getAgencyProfileUseCase(),
            getAgencyServicesUseCase()
        ) { profileRes, servicesRes ->
            _uiState.update { current ->
                val profile = (profileRes as? Resource.Success)?.data ?: current.agencyProfile
                val services = (servicesRes as? Resource.Success)?.data ?: current.services
                val isAnyLoading = profileRes is Resource.Loading || servicesRes is Resource.Loading
                val error = (profileRes as? Resource.Error)?.message
                    ?: (servicesRes as? Resource.Error)?.message

                current.copy(
                    isLoading = isAnyLoading,
                    agencyProfile = profile,
                    services = services,
                    errorMessage = error
                )
            }
        }.onStart {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        }.launchIn(viewModelScope)
    }
}
