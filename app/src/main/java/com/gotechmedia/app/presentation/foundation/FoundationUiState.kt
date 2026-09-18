package com.gotechmedia.app.presentation.foundation

import com.gotechmedia.app.domain.model.AgencyProfile
import com.gotechmedia.app.domain.model.AgencyService

/**
 * Architectural layer readiness verification item.
 */
data class ArchitectureLayerStatus(
    val name: String,
    val packageName: String,
    val description: String,
    val isReady: Boolean = true
)

/**
 * Immutable UI State for the GoTech Media foundation architecture.
 */
data class FoundationUiState(
    val isLoading: Boolean = false,
    val agencyProfile: AgencyProfile? = null,
    val services: List<AgencyService> = emptyList(),
    val architectureLayers: List<ArchitectureLayerStatus> = listOf(
        ArchitectureLayerStatus("Presentation", "com.gotechmedia.app.presentation", "Jetpack Compose, M3, ViewModels, StateFlow, Apple-inspired Minimal UI"),
        ArchitectureLayerStatus("Domain", "com.gotechmedia.app.domain", "Pure Kotlin models, Repository contracts, and single-purpose UseCases"),
        ArchitectureLayerStatus("Data", "com.gotechmedia.app.data", "DataSources, DTO entities, Repository implementation, Firebase ready"),
        ArchitectureLayerStatus("Navigation", "com.gotechmedia.app.navigation", "Type-safe Screen routes and decoupled NavigationActions"),
        ArchitectureLayerStatus("Dependency Injection", "com.gotechmedia.app.di", "Scalable AppContainer provider, Hilt singleton binding structure"),
        ArchitectureLayerStatus("Core & Utils", "com.gotechmedia.app.core", "AppConfig, Resource<T> result wrappers, and global constants"),
        ArchitectureLayerStatus("UI Theme", "com.gotechmedia.app.ui.theme", "Enforced dark mode only, Obsidian canvas, and custom typography")
    ),
    val errorMessage: String? = null
)
