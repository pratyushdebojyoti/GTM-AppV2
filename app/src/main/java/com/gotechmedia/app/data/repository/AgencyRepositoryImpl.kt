package com.gotechmedia.app.data.repository

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.data.datasource.AgencyDataSource
import com.gotechmedia.app.data.model.toDomain
import com.gotechmedia.app.domain.model.AgencyProcessStep
import com.gotechmedia.app.domain.model.AgencyProfile
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.ContactInquiry
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.model.QuoteRequest
import com.gotechmedia.app.domain.model.UserProfile
import com.gotechmedia.app.domain.repository.AgencyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of AgencyRepository.
 * Manages data mapping from entities to domain models with explicit Coroutine Dispatchers.
 */
class AgencyRepositoryImpl(
    private val dataSource: AgencyDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AgencyRepository {

    override fun getAgencyProfile(): Flow<Resource<AgencyProfile>> {
        return dataSource.streamProfile()
            .map { entity ->
                Resource.Success(entity.toDomain()) as Resource<AgencyProfile>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to stream agency profile", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override fun getAgencyServices(): Flow<Resource<List<AgencyService>>> {
        return dataSource.streamServices()
            .map { entities ->
                Resource.Success(entities.map { it.toDomain() }) as Resource<List<AgencyService>>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to stream agency services", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override fun getServiceById(serviceId: String): Flow<Resource<AgencyService?>> {
        return dataSource.streamServices()
            .map { entities ->
                val service = entities.find { it.id == serviceId }?.toDomain()
                Resource.Success(service) as Resource<AgencyService?>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to find service", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override fun getPortfolio(): Flow<Resource<List<PortfolioItem>>> {
        return dataSource.streamPortfolio()
            .map { entities ->
                Resource.Success(entities.map { it.toDomain() }) as Resource<List<PortfolioItem>>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to stream portfolio", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override fun getPortfolioById(portfolioId: String): Flow<Resource<PortfolioItem?>> {
        return dataSource.streamPortfolio()
            .map { entities ->
                val item = entities.find { it.id == portfolioId }?.toDomain()
                Resource.Success(item) as Resource<PortfolioItem?>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to find portfolio item", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override fun getProcessSteps(): Flow<Resource<List<AgencyProcessStep>>> {
        return dataSource.streamProcessSteps()
            .map { entities ->
                Resource.Success(entities.map { it.toDomain() }) as Resource<List<AgencyProcessStep>>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to stream process steps", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override fun getUserProfile(): Flow<Resource<UserProfile?>> {
        return dataSource.streamUserProfile()
            .map { entity ->
                Resource.Success(entity?.toDomain()) as Resource<UserProfile?>
            }
            .catch { throwable ->
                emit(Resource.Error(throwable.message ?: "Failed to stream user profile", throwable))
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun submitQuoteRequest(request: QuoteRequest): Resource<Unit> = withContext(ioDispatcher) {
        try {
            val result = dataSource.submitQuote(request)
            if (result.isSuccess) {
                Resource.Success(Unit)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to submit quote")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Submission error", e)
        }
    }

    override suspend fun submitContactInquiry(inquiry: ContactInquiry): Resource<Unit> = withContext(ioDispatcher) {
        try {
            val result = dataSource.submitContact(inquiry)
            if (result.isSuccess) {
                Resource.Success(Unit)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to send message")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Contact submission error", e)
        }
    }

    override suspend fun refreshData(): Resource<Unit> = withContext(ioDispatcher) {
        try {
            val result = dataSource.refresh()
            if (result.isSuccess) {
                Resource.Success(Unit)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Refresh failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown refresh error", e)
        }
    }
}
