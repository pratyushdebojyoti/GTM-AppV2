package com.gotechmedia.app.data.model

import com.gotechmedia.app.domain.model.AgencyMetric
import com.gotechmedia.app.domain.model.AgencyProcessStep
import com.gotechmedia.app.domain.model.AgencyProfile
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.model.UserProfile

/**
 * Data layer transfer entities (DTOs) for network, local, or future Firestore serialization.
 */
data class AgencyProfileEntity(
    val name: String = "",
    val headline: String = "",
    val mission: String = "",
    val foundedYear: Int = 0,
    val location: String = "",
    val specializations: List<String> = emptyList(),
    val metrics: List<AgencyMetricEntity> = emptyList()
)

data class AgencyMetricEntity(
    val label: String = "",
    val value: String = "",
    val description: String = ""
)

data class AgencyServiceEntity(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val summary: String = "",
    val capabilities: List<String> = emptyList(),
    val deliverables: List<String> = emptyList(),
    val methodology: String = "",
    val techStack: List<String> = emptyList(),
    val featured: Boolean = false
)

data class PortfolioItemEntity(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val industry: String = "",
    val summary: String = "",
    val challenge: String = "",
    val solution: String = "",
    val architectureHighlights: List<String> = emptyList(),
    val techStack: List<String> = emptyList(),
    val deliverables: List<String> = emptyList(),
    val isFeatured: Boolean = false
)

data class AgencyProcessStepEntity(
    val stepNumber: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val deliverables: List<String> = emptyList(),
    val duration: String = ""
)

data class UserProfileEntity(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val company: String = "",
    val role: String = "",
    val memberSince: String = "",
    val activeInquiriesCount: Int = 0
)

// Extension mappers to Domain
fun AgencyProfileEntity.toDomain(): AgencyProfile = AgencyProfile(
    name = name,
    headline = headline,
    mission = mission,
    foundedYear = foundedYear,
    location = location,
    specializations = specializations,
    metrics = metrics.map { it.toDomain() }
)

fun AgencyMetricEntity.toDomain(): AgencyMetric = AgencyMetric(
    label = label,
    value = value,
    description = description
)

fun AgencyServiceEntity.toDomain(): AgencyService = AgencyService(
    id = id,
    title = title,
    category = category,
    summary = summary,
    capabilities = capabilities,
    deliverables = deliverables,
    methodology = methodology,
    techStack = techStack,
    featured = featured
)

fun PortfolioItemEntity.toDomain(): PortfolioItem = PortfolioItem(
    id = id,
    title = title,
    category = category,
    industry = industry,
    summary = summary,
    challenge = challenge,
    solution = solution,
    architectureHighlights = architectureHighlights,
    techStack = techStack,
    deliverables = deliverables,
    isFeatured = isFeatured
)

fun AgencyProcessStepEntity.toDomain(): AgencyProcessStep = AgencyProcessStep(
    stepNumber = stepNumber,
    title = title,
    subtitle = subtitle,
    description = description,
    deliverables = deliverables,
    duration = duration
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    name = name,
    email = email,
    company = company,
    role = role,
    memberSince = memberSince,
    activeInquiriesCount = activeInquiriesCount
)
