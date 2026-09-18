package com.gotechmedia.app.presentation.screens.portfoliodetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.usecase.GetPortfolioDetailsUseCase
import com.gotechmedia.app.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PortfolioDetailsViewModel(
    private val portfolioId: String,
    private val getPortfolioDetailsUseCase: GetPortfolioDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<PortfolioItem>>(UiState.Loading)
    val uiState: StateFlow<UiState<PortfolioItem>> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        _uiState.value = UiState.Loading
        getPortfolioDetailsUseCase(portfolioId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(resource.message)
                }
                is Resource.Success -> {
                    val item = resource.data
                    if (item == null) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(item)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun retry() {
        loadDetails()
    }
}
