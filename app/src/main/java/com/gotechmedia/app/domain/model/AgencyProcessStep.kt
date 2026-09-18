package com.gotechmedia.app.domain.model

/**
 * Domain representation of GoTech Media's engineering and delivery process.
 */
data class AgencyProcessStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val deliverables: List<String>,
    val duration: String
)
