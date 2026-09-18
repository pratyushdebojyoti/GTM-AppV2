package com.gotechmedia.app.presentation.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.usecase.GetAgencyServicesUseCase
import com.gotechmedia.app.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ServicesViewModel(
    private val getAgencyServicesUseCase: GetAgencyServicesUseCase
) : ViewModel() {

    private val _rawServices = MutableStateFlow<List<AgencyService>>(emptyList())
    private val _uiState = MutableStateFlow<UiState<List<AgencyService>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<AgencyService>>> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadServices()
    }

    fun loadServices() {
        _uiState.value = UiState.Loading
        getAgencyServicesUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is Resource.Error -> {
                    _uiState.value = UiState.Error(resource.message)
                }
                is Resource.Success -> {
                    val list = resource.data
                    _rawServices.value = list
                    applyFilter()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        applyFilter()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        val category = _selectedCategory.value

        val filtered = _rawServices.value.filter { service ->
            val matchesCategory = if (category == "All") true else service.category.contains(category, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                service.title.lowercase().contains(query) ||
                        service.summary.lowercase().contains(query) ||
                        service.capabilities.any { it.lowercase().contains(query) }
            }
            matchesCategory && matchesQuery
        }

        if (filtered.isEmpty()) {
            if (_rawServices.value.isEmpty()) {
                _uiState.value = UiState.Empty
            } else {
                _uiState.value = UiState.Success(emptyList<AgencyService>())
            }
        } else {
            _uiState.value = UiState.Success(filtered)
        }
    }

    fun retry() {
        loadServices()
    }
}
