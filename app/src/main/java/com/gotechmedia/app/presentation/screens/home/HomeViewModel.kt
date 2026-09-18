package com.gotechmedia.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyProfile
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.usecase.GetAgencyProfileUseCase
import com.gotechmedia.app.domain.usecase.GetAgencyServicesUseCase
import com.gotechmedia.app.domain.usecase.GetPortfolioUseCase
import com.gotechmedia.app.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class HomeData(
    val profile: AgencyProfile,
    val featuredServices: List<AgencyService>,
    val featuredPortfolio: List<PortfolioItem>
)

class HomeViewModel(
    private val getAgencyProfileUseCase: GetAgencyProfileUseCase,
    private val getAgencyServicesUseCase: GetAgencyServicesUseCase,
    private val getPortfolioUseCase: GetPortfolioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = UiState.Loading
        combine(
            getAgencyProfileUseCase(),
            getAgencyServicesUseCase(),
            getPortfolioUseCase()
        ) { profileRes, servicesRes, portfolioRes ->
            when {
                profileRes is Resource.Error -> {
                    _uiState.value = UiState.Error(profileRes.message)
                }
                servicesRes is Resource.Error -> {
                    _uiState.value = UiState.Error(servicesRes.message)
                }
                portfolioRes is Resource.Error -> {
                    _uiState.value = UiState.Error(portfolioRes.message)
                }
                profileRes is Resource.Success && servicesRes is Resource.Success && portfolioRes is Resource.Success -> {
                    val profile = profileRes.data
                    val services = servicesRes.data
                    val portfolio = portfolioRes.data

                    if (services.isEmpty() && portfolio.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(
                            HomeData(
                                profile = profile,
                                featuredServices = services.filter { it.featured }.take(4).ifEmpty { services.take(4) },
                                featuredPortfolio = portfolio.filter { it.isFeatured }.take(3).ifEmpty { portfolio.take(3) }
                            )
                        )
                    }
                }
                else -> {
                    // Loading
                }
            }
        }.launchIn(viewModelScope)
    }

    fun retry() {
        loadData()
    }
}
