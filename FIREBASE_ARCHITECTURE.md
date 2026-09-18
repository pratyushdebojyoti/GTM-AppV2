# GoTech Media — Firebase Enterprise Architecture & Data Model Documentation

This document outlines the complete Firebase enterprise backend and client integration engineered for the GoTech Media Android platform.

---

## 1. Architectural Pattern

Adhering strictly to Clean Architecture and MVVM boundaries:

```
[ Composable UI Layer ]
       ↓
[ Jetpack ViewModel ] (StateFlow & SharedFlow events)
       ↓
[ Domain Use Cases ] (Pure business logic)
       ↓
[ Repository Abstractions ] (AuthRepository, FirestoreRepository, StorageRepository, NotificationRepository)
       ↓
[ Firebase Android SDKs ] (FirebaseAuth, Cloud Firestore, FirebaseStorage, FirebaseMessaging, FirebaseAnalytics)
```

**Zero direct Firebase calls are executed inside Composables.** All authentication transitions, Firestore listeners, and file uploads are dispatched via Coroutine Dispatchers (`Dispatchers.IO`) and mapped into type-safe Kotlin data models.

---

## 2. Authentication System

- **Email & Password Authentication**: Full validation, sanitized inputs, and secure user profile persistence in Cloud Firestore under `/users/{uid}`.
- **Google Sign-In**: Integrated with the Android Credential Manager API (`androidx.credentials`), nonces, and Google ID token verification against Firebase Auth without hardcoded credentials.
- **Forgot Password**: One-tap password reset link dispatch via Firebase Auth (`sendPasswordResetEmail`).
- **Session Lifecycle & Roles**:
  - `AuthState.Loading`: App initialization and credential resolution.
  - `AuthState.Unauthenticated`: Guest browsing mode with full access to public agency catalog (services, portfolio, testimonials, quote calculations).
  - `AuthState.Authenticated`: Client and administrative access with personalized project dashboards, deliverables, and support tickets.
  - `AuthState.Error`: Graceful error boundary reporting actionable feedback.
- **Roles**:
  - `PUBLIC`: Default guest visitor.
  - `CLIENT`: Verified enterprise client with isolated tenant access to projects and tickets.
  - `ADMIN`: GoTech Media executive and engineering administrative access.

---

## 3. Cloud Firestore Data Model & Schema

All 14 requested collections are mapped with strong typing and automated timestamp auditing.

### Collection: `users`
- Path: `/users/{userId}`
- Purpose: Core user authentication and profile registry.
- Fields:
  - `id`: String (Firebase Auth UID)
  - `email`: String (User email)
  - `displayName`: String (Full name)
  - `company`: String (Organization name)
  - `role`: String (`CLIENT`, `ADMIN`, `PUBLIC`)
  - `avatarUrl`: String? (Storage download URL)
  - `fcmToken`: String? (Push notification token)
  - `createdAtEpoch`: Long
  - `updatedAtEpoch`: Long

### Collection: `clients`
- Path: `/clients/{clientId}`
- Purpose: Enterprise accounts and organizational metadata.
- Fields:
  - `id`: String
  - `userId`: String (Primary billing/admin contact)
  - `name`: String
  - `industry`: String
  - `tier`: String (`STANDARD`, `GROWTH`, `ENTERPRISE_CORE`)
  - `totalContractValue`: Double
  - `activeProjectsCount`: Int
  - `primaryContactEmail`: String
  - `primaryContactPhone`: String
  - `createdAtEpoch`: Long

### Collection: `leads`
- Path: `/leads/{leadId}`
- Purpose: Incoming inquiries and custom quote requests.
- Fields:
  - `id`: String
  - `userId`: String?
  - `clientName`: String
  - `clientEmail`: String
  - `company`: String
  - `phone`: String
  - `selectedServices`: List<String>
  - `budgetTier`: String
  - `timeline`: String
  - `projectBrief`: String
  - `source`: String (`QUOTE_CALCULATOR`, `CONTACT_FORM`)
  - `status`: String (`NEW`, `IN_REVIEW`, `QUALIFIED`, `PROPOSAL_SENT`, `ARCHIVED`)
  - `createdAtEpoch`: Long

### Collection: `projects`
- Path: `/projects/{projectId}`
- Purpose: High-value enterprise deliverables and sprint status.
- Fields:
  - `id`: String
  - `clientId`: String
  - `userId`: String
  - `title`: String
  - `description`: String
  - `status`: String (`PROVISIONING`, `IN_DEVELOPMENT`, `SECURITY_AUDIT`, `DEPLOYED`, `MAINTENANCE`)
  - `progressPercentage`: Int (0 - 100)
  - `startDateEpoch`: Long
  - `targetDeliveryEpoch`: Long
  - `assignedTechLead`: String
  - `repositoryUrl`: String?
  - `createdAtEpoch`: Long

#### Subcollection: `projects/{projectId}/milestones`
- Fields: `id`, `projectId`, `title`, `description`, `sequenceOrder`, `isCompleted`, `targetCompletionEpoch`, `completedEpoch`

#### Subcollection: `projects/{projectId}/documents`
- Fields: `id`, `projectId`, `name`, `type`, `storagePath`, `downloadUrl`, `fileSizeBytes`, `uploadedBy`, `uploadedAtEpoch`

### Collection: `services`
- Path: `/services/{serviceId}`
- Purpose: Digital capabilities catalog (14 GoTech engineering practices).
- Fields: `id`, `title`, `category`, `summary`, `capabilities`, `deliverables`, `methodology`, `techStack`, `featured`

### Collection: `portfolio`
- Path: `/portfolio/{portfolioId}`
- Purpose: Architectural case studies with measurable business outcomes.
- Fields: `id`, `title`, `category`, `industry`, `summary`, `challenge`, `solution`, `architectureHighlights`, `techStack`, `deliverables`, `isFeatured`

### Collection: `testimonials`
- Path: `/testimonials/{testimonialId}`
- Purpose: Enterprise client endorsements and ratings.
- Fields: `id`, `clientName`, `clientRole`, `company`, `quote`, `rating`, `featured`, `createdAtEpoch`

### Collection: `notifications`
- Path: `/notifications/{notificationId}`
- Purpose: Transactional and milestone push alerts.
- Fields: `id`, `recipientId` (UID or "ALL"), `title`, `body`, `type`, `targetRoute`, `isRead`, `createdAtEpoch`

### Collection: `messages`
- Path: `/messages/{messageId}`
- Purpose: Secure real-time communications for enterprise projects.
- Fields: `id`, `conversationId`, `senderId`, `senderName`, `content`, `attachmentUrl`, `readBy`, `sentAtEpoch`

### Collection: `supportTickets`
- Path: `/supportTickets/{ticketId}`
- Purpose: Enterprise support SLA handling and bug resolution.
- Fields: `id`, `clientId`, `userId`, `subject`, `description`, `priority` (`LOW`, `NORMAL`, `HIGH`, `CRITICAL`), `status` (`OPEN`, `INVESTIGATING`, `RESOLVED`, `CLOSED`), `assignedEngineer`, `createdAtEpoch`, `updatedAtEpoch`

### Collection: `agencySettings`
- Path: `/agencySettings/{settingId}`
- Purpose: Global app configuration (maintenance toggle, contact info, minimum required version).
- Fields: `id`, `maintenanceMode`, `minimumSupportedVersion`, `contactEmail`, `officeAddress`, `hotline`

### Collection: `activityLogs`
- Path: `/activityLogs/{logId}`
- Purpose: Immutable audit trail for compliance and SOC2-ready tracking.
- Fields: `id`, `userId`, `action`, `resource`, `details`, `timestampEpoch`

---

## 4. Security Rules Specification

### Cloud Firestore Security Rules (`firestore.rules`)
- **Strict Role-Based Multi-Tenancy**: Clients are restricted exclusively to their own user document, client records, assigned projects, and submitted tickets.
- **Admin Oversight**: Admins have supervisory read/write capabilities across the operational backend.
- **Public Read Surfaces**: `services`, `portfolio`, `testimonials`, and `agencySettings` are open for guest reads, preventing empty states on cold starts.
- **Public Inquiries**: Unauthenticated users can submit quote inquiries into `/leads`, while reads remain strictly gated to Admins.

### Firebase Storage Security Rules (`storage.rules`)
- `/public/*`: Read-accessible to all, write-accessible strictly to Admins.
- `/clients/{userId}/*`: Read and upload restricted to the verified resource owner and Admins (max size 25MB).
- `/projects/{projectId}/*`: Read and upload restricted to assigned enterprise stakeholders and Admins (max size 50MB).
- All unauthorized root storage operations are denied by default.
