package com.gotechmedia.app.domain.model

/**
 * Domain representation of a custom quote request submitted by a client.
 */
data class QuoteRequest(
    val id: String = "",
    val clientName: String,
    val clientEmail: String,
    val company: String,
    val selectedServices: List<String>,
    val timeline: String,
    val budgetTier: String,
    val projectBrief: String,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Direct contact inquiry submission.
 */
data class ContactInquiry(
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

/**
 * User/Client profile domain model.
 */
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val company: String,
    val role: String,
    val memberSince: String,
    val activeInquiriesCount: Int = 0
)
