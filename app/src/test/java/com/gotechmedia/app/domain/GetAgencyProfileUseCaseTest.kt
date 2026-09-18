package com.gotechmedia.app.domain

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.data.datasource.InMemoryAgencyDataSource
import com.gotechmedia.app.data.repository.AgencyRepositoryImpl
import com.gotechmedia.app.domain.usecase.GetAgencyProfileUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAgencyProfileUseCaseTest {

    @Test
    fun `usecase emits agency profile from repository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val dataSource = InMemoryAgencyDataSource()
        val repository = AgencyRepositoryImpl(dataSource, testDispatcher)
        val useCase = GetAgencyProfileUseCase(repository)

        val result = useCase().first()

        assertTrue(result is Resource.Success)
        val profile = (result as Resource.Success).data
        assertEquals("GoTech Media", profile.name)
        assertTrue(profile.specializations.isNotEmpty())
    }
}
