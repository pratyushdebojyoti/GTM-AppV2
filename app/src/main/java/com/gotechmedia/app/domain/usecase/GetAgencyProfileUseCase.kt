package com.gotechmedia.app.domain.usecase

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyProfile
import com.gotechmedia.app.domain.repository.AgencyRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase to fetch and stream agency profile data.
 * Pure business logic with single responsibility.
 */
class GetAgencyProfileUseCase(
    private val repository: AgencyRepository
) {
    operator fun invoke(): Flow<Resource<AgencyProfile>> {
        return repository.getAgencyProfile()
    }
}
