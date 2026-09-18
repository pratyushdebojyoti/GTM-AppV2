package com.gotechmedia.app.domain.model

/**
 * Domain representation of digital services offered by GoTech Media.
 * Pure Kotlin business entity.
 */
data class AgencyService(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val capabilities: List<String>,
    val deliverables: List<String> = emptyList(),
    val methodology: String = "",
    val techStack: List<String> = emptyList(),
    val featured: Boolean = false
)
