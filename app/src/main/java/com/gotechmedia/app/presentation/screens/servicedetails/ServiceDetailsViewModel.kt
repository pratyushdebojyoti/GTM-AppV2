package com.gotechmedia.app.presentation.screens.servicedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.usecase.GetServiceDetailsUseCase
import com.gotechmedia.app.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ServiceDetailsViewModel(
    private val serviceId: String,
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AgencyService>>(UiState.Loading)
    val uiState: StateFlow<UiState<AgencyService>> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        _uiState.value = UiState.Loading
        getServiceDetailsUseCase(serviceId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(resource.message ?: "Failed to load service details")
                }
                is Resource.Success -> {
                    val service = resource.data
                    if (service == null) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(service)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun retry() {
        loadDetails()
    }
}
