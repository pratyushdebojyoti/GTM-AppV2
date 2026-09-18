package com.gotechmedia.app.domain.repository

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyProcessStep
import com.gotechmedia.app.domain.model.AgencyProfile
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.ContactInquiry
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.model.QuoteRequest
import com.gotechmedia.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Domain boundary contract for agency operations.
 * Implements Inversion of Control. All presentation use cases communicate via this interface.
 */
interface AgencyRepository {
    fun getAgencyProfile(): Flow<Resource<AgencyProfile>>
    fun getAgencyServices(): Flow<Resource<List<AgencyService>>>
    fun getServiceById(serviceId: String): Flow<Resource<AgencyService?>>
    fun getPortfolio(): Flow<Resource<List<PortfolioItem>>>
    fun getPortfolioById(portfolioId: String): Flow<Resource<PortfolioItem?>>
    fun getProcessSteps(): Flow<Resource<List<AgencyProcessStep>>>
    fun getUserProfile(): Flow<Resource<UserProfile?>>
    suspend fun submitQuoteRequest(request: QuoteRequest): Resource<Unit>
    suspend fun submitContactInquiry(inquiry: ContactInquiry): Resource<Unit>
    suspend fun refreshData(): Resource<Unit>
}
