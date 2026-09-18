package com.gotechmedia.app.domain.usecase

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.repository.AgencyRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase to fetch and stream GoTech Media capabilities & service catalog.
 */
class GetAgencyServicesUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(): Flow<Resource<List<AgencyService>>> {
        return repository.getAgencyServices()
    }
}
