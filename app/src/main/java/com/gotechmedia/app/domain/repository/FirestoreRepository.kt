package com.gotechmedia.app.domain.repository

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyActivityLog
import com.gotechmedia.app.domain.model.AgencyClient
import com.gotechmedia.app.domain.model.AgencyDocument
import com.gotechmedia.app.domain.model.AgencyLead
import com.gotechmedia.app.domain.model.AgencyMessage
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.AgencyProjectMilestone
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.AgencySettings
import com.gotechmedia.app.domain.model.AgencySupportTicket
import com.gotechmedia.app.domain.model.AgencyTestimonial
import com.gotechmedia.app.domain.model.AgencyUser
import kotlinx.coroutines.flow.Flow

/**
 * Cloud Firestore Repository contract.
 * Pure abstraction covering all required agency collections:
 * users, clients, leads, projects, milestones, services, portfolio,
 * testimonials, notifications, messages, supportTickets, documents,
 * agencySettings, activityLogs.
 */
interface FirestoreRepository {

    // Users & Clients
    fun getUserProfile(userId: String): Flow<Resource<AgencyUser>>
    fun observeUserProfile(userId: String): Flow<Resource<AgencyUser>>
    fun saveUserProfile(user: AgencyUser): Flow<Resource<Unit>>
    fun getClientRecord(clientId: String): Flow<Resource<AgencyClient?>>

    // Public Agency Data (Services, Portfolio, Testimonials)
    fun getServices(): Flow<Resource<List<AgencyService>>>
    fun getServiceById(serviceId: String): Flow<Resource<AgencyService?>>
    fun getPortfolio(): Flow<Resource<List<PortfolioItem>>>
    fun getPortfolioById(portfolioId: String): Flow<Resource<PortfolioItem?>>
    fun getTestimonials(): Flow<Resource<List<AgencyTestimonial>>>

    // Leads & Inquiries
    fun submitLead(lead: AgencyLead): Flow<Resource<String>>
    fun getLeads(): Flow<Resource<List<AgencyLead>>>

    // Client Projects & Milestones
    fun getClientProjects(userId: String): Flow<Resource<List<AgencyProject>>>
    fun getProjectById(projectId: String): Flow<Resource<AgencyProject?>>
    fun getProjectMilestones(projectId: String): Flow<Resource<List<AgencyProjectMilestone>>>
    fun getProjectDocuments(projectId: String): Flow<Resource<List<AgencyDocument>>>

    // Support Tickets
    fun submitSupportTicket(ticket: AgencySupportTicket): Flow<Resource<String>>
    fun getClientSupportTickets(userId: String): Flow<Resource<List<AgencySupportTicket>>>

    // Messages & Realtime Conversations
    fun getMessages(conversationId: String): Flow<Resource<List<AgencyMessage>>>
    fun sendMessage(message: AgencyMessage): Flow<Resource<String>>

    // Notifications
    fun observeUserNotifications(userId: String): Flow<Resource<List<AgencyNotification>>>
    fun markNotificationAsRead(notificationId: String): Flow<Resource<Unit>>

    // Agency Settings & Audit Activity Logs
    fun getAgencySettings(): Flow<Resource<AgencySettings>>
    fun logActivity(activity: AgencyActivityLog): Flow<Resource<Unit>>
}
