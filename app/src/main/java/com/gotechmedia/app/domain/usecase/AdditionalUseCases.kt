package com.gotechmedia.app.domain.usecase

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.repository.AgencyRepository
import kotlinx.coroutines.flow.Flow

class GetPortfolioUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(): Flow<Resource<List<PortfolioItem>>> {
        return repository.getPortfolio()
    }
}

class GetPortfolioDetailsUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(portfolioId: String): Flow<Resource<PortfolioItem?>> {
        return repository.getPortfolioById(portfolioId)
    }
}

class GetServiceDetailsUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(serviceId: String): Flow<Resource<com.gotechmedia.app.domain.model.AgencyService?>> {
        return repository.getServiceById(serviceId)
    }
}

class GetProcessStepsUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(): Flow<Resource<List<com.gotechmedia.app.domain.model.AgencyProcessStep>>> {
        return repository.getProcessSteps()
    }
}

class GetUserProfileUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(): Flow<Resource<com.gotechmedia.app.domain.model.UserProfile?>> {
        return repository.getUserProfile()
    }
}

class SubmitQuoteUseCase(
    private val repository: AgencyRepository
) {
    suspend operator fun invoke(request: com.gotechmedia.app.domain.model.QuoteRequest): Resource<Unit> {
        return repository.submitQuoteRequest(request)
    }
}

class SubmitContactUseCase(
    private val repository: AgencyRepository
) {
    suspend operator fun invoke(inquiry: com.gotechmedia.app.domain.model.ContactInquiry): Resource<Unit> {
        return repository.submitContactInquiry(inquiry)
    }
}
