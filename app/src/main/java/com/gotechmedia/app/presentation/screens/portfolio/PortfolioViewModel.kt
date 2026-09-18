package com.gotechmedia.app.presentation.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.usecase.GetPortfolioUseCase
import com.gotechmedia.app.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PortfolioViewModel(
    private val getPortfolioUseCase: GetPortfolioUseCase
) : ViewModel() {

    private val _rawItems = MutableStateFlow<List<PortfolioItem>>(emptyList())
    private val _uiState = MutableStateFlow<UiState<List<PortfolioItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<PortfolioItem>>> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    init {
        loadPortfolio()
    }

    fun loadPortfolio() {
        _uiState.value = UiState.Loading
        getPortfolioUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(resource.message)
                }
                is Resource.Success -> {
                    val list = resource.data
                    _rawItems.value = list
                    applyFilter()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        val filter = _selectedFilter.value
        val items = _rawItems.value

        val filtered = if (filter == "All") {
            items
        } else {
            items.filter { it.category.contains(filter, ignoreCase = true) || it.industry.contains(filter, ignoreCase = true) }
        }

        if (filtered.isEmpty() && items.isEmpty()) {
            _uiState.value = UiState.Empty
        } else {
            _uiState.value = UiState.Success(filtered)
        }
    }

    fun retry() {
        loadPortfolio()
    }
}
