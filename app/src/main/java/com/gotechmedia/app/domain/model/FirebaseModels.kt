package com.gotechmedia.app.domain.model

import com.gotechmedia.app.core.auth.UserRole

/**
 * Domain representation of an authenticated GoTech Media user account in Firestore `users`.
 */
data class AgencyUser(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val role: UserRole = UserRole.PUBLIC,
    val company: String = "",
    val phone: String = "",
    val fcmToken: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val updatedAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Enterprise client record in Firestore `clients`.
 */
data class AgencyClient(
    val id: String = "",
    val userId: String = "",
    val companyName: String = "",
    val industry: String = "",
    val tier: String = "Enterprise",
    val status: String = "ACTIVE",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val assignedAccountManager: String = "GoTech Media Executive",
    val createdAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Standard enterprise lead lifecycle stages.
 */
enum class LeadStatus(val displayName: String) {
    NEW("New"),
    CONTACTED("Contacted"),
    QUALIFIED("Qualified"),
    PROPOSAL("Proposal"),
    WON("Won"),
    LOST("Lost"),
    ARCHIVED("Archived");

    companion object {
        fun fromString(value: String): LeadStatus =
            entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.displayName.equals(value, ignoreCase = true)
            } ?: NEW
    }
}

/**
 * Prospect or customer lead in Firestore `leads` (e.g. quote requests, inquiries).
 */
data class AgencyLead(
    val id: String = "",
    val leadReferenceId: String = "",
    val userId: String? = null,
    val clientName: String = "",
    val email: String = "",
    val company: String = "",
    val phone: String = "",
    val serviceRequested: String = "",
    val selectedServices: List<String> = emptyList(),
    val projectDescription: String = "",
    val budgetRange: String = "",
    val expectedTimeline: String = "",
    val referenceWebsite: String = "",
    val attachmentUrl: String? = null,
    val attachmentName: String? = null,
    val attachmentSizeBytes: Long = 0L,
    val estimatedBudget: String = budgetRange,
    val timeline: String = expectedTimeline,
    val notes: String = projectDescription,
    val source: String = "REQUEST_A_QUOTE",
    val status: String = LeadStatus.NEW.displayName,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Standard enterprise project delivery stages.
 * Project status can ONLY be updated by Admins; client UI is strictly read-only.
 */
enum class ProjectStatus(val displayName: String) {
    INQUIRY("Inquiry"),
    PLANNING("Planning"),
    DESIGN("Design"),
    DEVELOPMENT("Development"),
    TESTING("Testing"),
    REVIEW("Review"),
    LAUNCH("Launch"),
    COMPLETED("Completed"),
    ON_HOLD("On Hold");

    companion object {
        fun fromString(value: String): ProjectStatus =
            entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.displayName.equals(value, ignoreCase = true)
            } ?: DEVELOPMENT
    }
}

/**
 * Deliverable milestone execution status.
 */
enum class MilestoneStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    companion object {
        fun fromString(value: String): MilestoneStatus =
            entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.displayName.equals(value, ignoreCase = true)
            } ?: PENDING
    }
}

/**
 * Support ticket priority level.
 */
enum class TicketPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    companion object {
        fun fromString(value: String): TicketPriority =
            entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.displayName.equals(value, ignoreCase = true)
            } ?: MEDIUM
    }
}

/**
 * Support ticket resolution lifecycle status.
 */
enum class TicketStatus(val displayName: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    companion object {
        fun fromString(value: String): TicketStatus =
            entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.displayName.equals(value, ignoreCase = true)
            } ?: OPEN
    }
}

/**
 * Assigned team member working on a client project.
 */
data class AgencyTeamMember(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val avatarUrl: String? = null,
    val email: String = ""
)

/**
 * Active or historical client software project in Firestore `projects`.
 */
data class AgencyProject(
    val id: String = "",
    val clientId: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = ProjectStatus.DEVELOPMENT.displayName,
    val progressPercentage: Int = 0,
    val budget: String = "",
    val startDate: String = "",
    val expectedCompletion: String = "",
    val targetDeliveryDate: String = expectedCompletion,
    val assignedTeam: List<AgencyTeamMember> = emptyList(),
    val techStack: List<String> = emptyList(),
    val githubRepo: String? = null,
    val stagingUrl: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val updatedAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Deliverable milestone within a client project in Firestore `projects/{projectId}/milestones`.
 */
data class AgencyProjectMilestone(
    val id: String = "",
    val projectId: String = "",
    val stepNumber: Int = 1,
    val title: String = "",
    val description: String = "",
    val status: String = MilestoneStatus.PENDING.displayName,
    val dueDate: String = "",
    val completionDate: String? = null,
    val deliverables: List<String> = emptyList()
)

/**
 * Project document or contractual deliverable in Firestore `projects/{projectId}/documents` or `documents`.
 */
data class AgencyDocument(
    val id: String = "",
    val projectId: String = "",
    val clientId: String = "",
    val title: String = "",
    val type: String = "ARCHITECTURE_SPEC", // NDA, SOW, ARCHITECTURE_SPEC, INVOICE, DELIVERABLE
    val fileUrl: String = "",
    val storagePath: String = "",
    val sizeBytes: Long = 0L,
    val uploadedBy: String = "",
    val createdAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Client testimonial in Firestore `testimonials`.
 */
data class AgencyTestimonial(
    val id: String = "",
    val clientName: String = "",
    val role: String = "",
    val company: String = "",
    val quote: String = "",
    val rating: Int = 5,
    val avatarUrl: String? = null,
    val isFeatured: Boolean = true,
    val verified: Boolean = true,
    val order: Int = 0
)

/**
 * Standard notification types supported across Android client and Admin Console.
 */
enum class NotificationType(
    val code: String,
    val displayName: String,
    val defaultTitle: String,
    val category: String
) {
    NEW_LEAD("NEW_LEAD", "New lead", "New Enterprise Lead Received", "Leads"),
    PROJECT_UPDATE("PROJECT_UPDATE", "Project update", "Project Milestone & Sprint Update", "Projects"),
    MILESTONE_UPDATE("MILESTONE_UPDATE", "Milestone update", "Sprint Milestone Delivered", "Milestones"),
    NEW_MESSAGE("NEW_MESSAGE", "New message", "New Project Communication", "Messages"),
    SUPPORT_TICKET_UPDATE("SUPPORT_TICKET_UPDATE", "Support ticket update", "Support Ticket Status Updated", "Support"),
    GENERAL_ANNOUNCEMENT("GENERAL_ANNOUNCEMENT", "General announcement", "GoTech Media Executive Announcement", "Announcements");

    companion object {
        fun fromString(value: String): NotificationType {
            return entries.find {
                it.code.equals(value, ignoreCase = true) ||
                        it.name.equals(value, ignoreCase = true) ||
                        it.displayName.equals(value, ignoreCase = true)
            } ?: GENERAL_ANNOUNCEMENT
        }
    }
}

/**
 * Direct user or system push notification in Firestore `notifications`.
 */
data class AgencyNotification(
    val id: String = "",
    val recipientId: String = "", // specific userId, "ADMINS", or "ALL"
    val title: String = "",
    val body: String = "",
    val type: String = NotificationType.GENERAL_ANNOUNCEMENT.code,
    val read: Boolean = false,
    val timestampEpoch: Long = System.currentTimeMillis(),
    val deepLink: String? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    val notificationType: NotificationType
        get() = NotificationType.fromString(type)
}

/**
 * Real-time project communication message in Firestore `messages`.
 */
data class AgencyMessage(
    val id: String = "",
    val conversationId: String = "",
    val projectId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "CLIENT", // CLIENT, ADMIN
    val content: String = "",
    val attachmentUrls: List<String> = emptyList(),
    val timestampEpoch: Long = System.currentTimeMillis(),
    val readBy: List<String> = emptyList()
)

/**
 * Client enterprise support ticket in Firestore `supportTickets`.
 */
data class AgencySupportTicket(
    val id: String = "",
    val clientId: String = "",
    val userId: String = "",
    val subject: String = "",
    val description: String = "",
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val updatedAtEpoch: Long = System.currentTimeMillis()
)

/**
 * Global agency operational configuration in Firestore `agencySettings/config`.
 */
data class AgencySettings(
    val agencyName: String = "GoTech Media",
    val contactEmail: String = "executive@gotechmedia.com",
    val phone: String = "+1 (800) 555-GOTECH",
    val calendlyUrl: String = "https://calendly.com/gotech-media",
    val officeLocations: List<String> = listOf("San Francisco, CA", "Zurich, Switzerland", "Singapore"),
    val maintenanceMode: Boolean = false,
    val minimumAppVersion: String = "1.0.0",
    val broadcastBanner: String? = null
)

/**
 * Audit and governance trail in Firestore `activityLogs`.
 */
data class AgencyActivityLog(
    val id: String = "",
    val userId: String = "",
    val actorName: String = "",
    val actorRole: String = "PUBLIC",
    val action: String = "",
    val entityType: String = "",
    val entityId: String = "",
    val timestampEpoch: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)
