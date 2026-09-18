package com.gotechmedia.app.domain.model

/**
 * Domain representation of a digital engineering portfolio case study.
 * Grounded in technical architecture, concrete problem statements, and real engineering deliverables.
 */
data class PortfolioItem(
    val id: String,
    val title: String,
    val category: String,
    val industry: String,
    val summary: String,
    val challenge: String,
    val solution: String,
    val architectureHighlights: List<String>,
    val techStack: List<String>,
    val deliverables: List<String>,
    val isFeatured: Boolean = false
)
