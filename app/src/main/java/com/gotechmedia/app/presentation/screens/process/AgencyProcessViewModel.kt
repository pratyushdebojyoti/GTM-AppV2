package com.gotechmedia.app.presentation.screens.process

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyProcessStep
import com.gotechmedia.app.domain.usecase.GetProcessStepsUseCase
import com.gotechmedia.app.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AgencyProcessViewModel(
    private val getProcessStepsUseCase: GetProcessStepsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<AgencyProcessStep>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<AgencyProcessStep>>> = _uiState.asStateFlow()

    init {
        loadSteps()
    }

    fun loadSteps() {
        _uiState.value = UiState.Loading
        getProcessStepsUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(resource.message)
                }
                is Resource.Success -> {
                    val steps = resource.data
                    if (steps.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(steps)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun retry() {
        loadSteps()
    }
}
