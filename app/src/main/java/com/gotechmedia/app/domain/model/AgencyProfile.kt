package com.gotechmedia.app.domain.model

/**
 * Domain representation of the GoTech Media agency profile.
 * Completely independent of Android frameworks, Firebase, or external serialization.
 */
data class AgencyProfile(
    val name: String,
    val headline: String,
    val mission: String,
    val foundedYear: Int,
    val location: String,
    val specializations: List<String>,
    val metrics: List<AgencyMetric>
)

data class AgencyMetric(
    val label: String,
    val value: String,
    val description: String
)
