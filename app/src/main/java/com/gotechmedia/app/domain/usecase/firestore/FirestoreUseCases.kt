package com.gotechmedia.app.domain.usecase.firestore

import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.AgencyLead
import com.gotechmedia.app.domain.model.AgencyNotification
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.domain.model.AgencySupportTicket
import com.gotechmedia.app.domain.model.AgencyUser
import com.gotechmedia.app.domain.repository.FirestoreRepository
import kotlinx.coroutines.flow.Flow

class SubmitLeadUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(lead: AgencyLead): Flow<Resource<String>> =
        repository.submitLead(lead)
}

class GetClientProjectsUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(userId: String): Flow<Resource<List<AgencyProject>>> =
        repository.getClientProjects(userId)
}

class GetClientTicketsUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(userId: String): Flow<Resource<List<AgencySupportTicket>>> =
        repository.getClientSupportTickets(userId)
}

class SubmitSupportTicketUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(ticket: AgencySupportTicket): Flow<Resource<String>> =
        repository.submitSupportTicket(ticket)
}

class ObserveUserNotificationsUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(userId: String): Flow<Resource<List<AgencyNotification>>> =
        repository.observeUserNotifications(userId)
}

class ObserveUserProfileUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(userId: String): Flow<Resource<AgencyUser>> =
        repository.observeUserProfile(userId)
}

class GetProjectDetailsUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(projectId: String): Flow<Resource<AgencyProject?>> =
        repository.getProjectById(projectId)
}

class GetProjectMilestonesUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(projectId: String): Flow<Resource<List<com.gotechmedia.app.domain.model.AgencyProjectMilestone>>> =
        repository.getProjectMilestones(projectId)
}

class GetProjectDocumentsUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(projectId: String): Flow<Resource<List<com.gotechmedia.app.domain.model.AgencyDocument>>> =
        repository.getProjectDocuments(projectId)
}

class GetProjectMessagesUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(conversationId: String): Flow<Resource<List<com.gotechmedia.app.domain.model.AgencyMessage>>> =
        repository.getMessages(conversationId)
}

class SendProjectMessageUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(message: com.gotechmedia.app.domain.model.AgencyMessage): Flow<Resource<String>> =
        repository.sendMessage(message)
}

class MarkNotificationAsReadUseCase(private val repository: FirestoreRepository) {
    operator fun invoke(notificationId: String): Flow<Resource<Unit>> =
        repository.markNotificationAsRead(notificationId)
}
