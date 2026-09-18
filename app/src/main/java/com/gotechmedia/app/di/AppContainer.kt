package com.gotechmedia.app.di

import android.content.Context
import com.gotechmedia.app.core.auth.GoogleSignInHelper
import com.gotechmedia.app.data.datasource.AgencyDataSource
import com.gotechmedia.app.data.datasource.InMemoryAgencyDataSource
import com.gotechmedia.app.data.firebase.AuthRepositoryImpl
import com.gotechmedia.app.data.firebase.FirebaseAnalyticsHelper
import com.gotechmedia.app.data.firebase.FirestoreRepositoryImpl
import com.gotechmedia.app.data.firebase.NotificationRepositoryImpl
import com.gotechmedia.app.data.firebase.StorageRepositoryImpl
import com.gotechmedia.app.data.repository.AgencyRepositoryImpl
import com.gotechmedia.app.domain.repository.AgencyRepository
import com.gotechmedia.app.domain.repository.AuthRepository
import com.gotechmedia.app.domain.repository.FirestoreRepository
import com.gotechmedia.app.domain.repository.NotificationRepository
import com.gotechmedia.app.domain.repository.StorageRepository
import com.gotechmedia.app.domain.usecase.GetAgencyProfileUseCase
import com.gotechmedia.app.domain.usecase.GetAgencyServicesUseCase
import com.gotechmedia.app.domain.usecase.GetPortfolioDetailsUseCase
import com.gotechmedia.app.domain.usecase.GetPortfolioUseCase
import com.gotechmedia.app.domain.usecase.GetProcessStepsUseCase
import com.gotechmedia.app.domain.usecase.GetServiceDetailsUseCase
import com.gotechmedia.app.domain.usecase.GetUserProfileUseCase
import com.gotechmedia.app.domain.usecase.SubmitContactUseCase
import com.gotechmedia.app.domain.usecase.SubmitQuoteUseCase
import com.gotechmedia.app.domain.usecase.auth.GetAuthStateUseCase
import com.gotechmedia.app.domain.usecase.auth.SendPasswordResetUseCase
import com.gotechmedia.app.domain.usecase.auth.SignInWithEmailUseCase
import com.gotechmedia.app.domain.usecase.auth.SignInWithGoogleUseCase
import com.gotechmedia.app.domain.usecase.auth.SignOutUseCase
import com.gotechmedia.app.domain.usecase.auth.SignUpWithEmailUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetClientProjectsUseCase
import com.gotechmedia.app.domain.usecase.firestore.GetClientTicketsUseCase
import com.gotechmedia.app.domain.usecase.firestore.ObserveUserNotificationsUseCase
import com.gotechmedia.app.domain.usecase.firestore.ObserveUserProfileUseCase
import com.gotechmedia.app.domain.usecase.firestore.SubmitLeadUseCase
import com.gotechmedia.app.domain.usecase.firestore.SubmitQuoteLeadUseCase
import com.gotechmedia.app.domain.usecase.firestore.SubmitSupportTicketUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Central Dependency Injection Container contract.
 * Provides decoupled access to all application dependencies (Firebase Repositories,
 * Use Cases, Utilities, Dispatchers).
 */
interface AppContainer {
    val ioDispatcher: CoroutineDispatcher
    val defaultDispatcher: CoroutineDispatcher

    // Core Legacy & Fallback
    val agencyDataSource: AgencyDataSource
    val agencyRepository: AgencyRepository

    // Firebase Repositories
    val authRepository: AuthRepository
    val firestoreRepository: FirestoreRepository
    val storageRepository: StorageRepository
    val notificationRepository: NotificationRepository

    // Helpers
    val googleSignInHelper: GoogleSignInHelper
    val analyticsHelper: FirebaseAnalyticsHelper

    // Agency Catalog Use Cases
    val getAgencyProfileUseCase: GetAgencyProfileUseCase
    val getAgencyServicesUseCase: GetAgencyServicesUseCase
    val getServiceDetailsUseCase: GetServiceDetailsUseCase
    val getPortfolioUseCase: GetPortfolioUseCase
    val getPortfolioDetailsUseCase: GetPortfolioDetailsUseCase
    val getProcessStepsUseCase: GetProcessStepsUseCase
    val getUserProfileUseCase: GetUserProfileUseCase
    val submitQuoteUseCase: SubmitQuoteUseCase
    val submitContactUseCase: SubmitContactUseCase

    // Auth Use Cases
    val getAuthStateUseCase: GetAuthStateUseCase
    val signInWithEmailUseCase: SignInWithEmailUseCase
    val signUpWithEmailUseCase: SignUpWithEmailUseCase
    val signInWithGoogleUseCase: SignInWithGoogleUseCase
    val sendPasswordResetUseCase: SendPasswordResetUseCase
    val signOutUseCase: SignOutUseCase

    // Firestore Use Cases
    val submitLeadUseCase: SubmitLeadUseCase
    val submitQuoteLeadUseCase: SubmitQuoteLeadUseCase
    val getClientProjectsUseCase: GetClientProjectsUseCase
    val getClientTicketsUseCase: GetClientTicketsUseCase
    val submitSupportTicketUseCase: SubmitSupportTicketUseCase
    val observeUserNotificationsUseCase: ObserveUserNotificationsUseCase
    val observeUserProfileUseCase: ObserveUserProfileUseCase
    val getProjectDetailsUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectDetailsUseCase
    val getProjectMilestonesUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectMilestonesUseCase
    val getProjectDocumentsUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectDocumentsUseCase
    val getProjectMessagesUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectMessagesUseCase
    val sendProjectMessageUseCase: com.gotechmedia.app.domain.usecase.firestore.SendProjectMessageUseCase
    val markNotificationAsReadUseCase: com.gotechmedia.app.domain.usecase.firestore.MarkNotificationAsReadUseCase
    val markAgencyNotificationAsReadUseCase: com.gotechmedia.app.domain.usecase.notification.MarkNotificationAsReadUseCase
    val markAllNotificationsAsReadUseCase: com.gotechmedia.app.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
    val deleteNotificationUseCase: com.gotechmedia.app.domain.usecase.notification.DeleteNotificationUseCase
    val registerDeviceTokenUseCase: com.gotechmedia.app.domain.usecase.notification.RegisterDeviceTokenUseCase
    val createNotificationUseCase: com.gotechmedia.app.domain.usecase.notification.CreateNotificationUseCase
    val notificationCenterObserveNotificationsUseCase: com.gotechmedia.app.domain.usecase.notification.ObserveUserNotificationsUseCase
}

/**
 * Default production implementation of AppContainer.
 * Lazily instantiates singletons and maintains clean separation of concerns.
 */
class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    override val ioDispatcher: CoroutineDispatcher by lazy {
        Dispatchers.IO
    }

    override val defaultDispatcher: CoroutineDispatcher by lazy {
        Dispatchers.Default
    }

    override val agencyDataSource: AgencyDataSource by lazy {
        InMemoryAgencyDataSource()
    }

    override val agencyRepository: AgencyRepository by lazy {
        AgencyRepositoryImpl(
            dataSource = agencyDataSource,
            ioDispatcher = ioDispatcher
        )
    }

    // Firebase Repositories
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(ioDispatcher = ioDispatcher)
    }

    override val firestoreRepository: FirestoreRepository by lazy {
        FirestoreRepositoryImpl(ioDispatcher = ioDispatcher, fallbackDataSource = agencyDataSource as InMemoryAgencyDataSource)
    }

    override val storageRepository: StorageRepository by lazy {
        StorageRepositoryImpl(ioDispatcher = ioDispatcher)
    }

    override val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(ioDispatcher = ioDispatcher)
    }

    // Helpers
    override val googleSignInHelper: GoogleSignInHelper by lazy {
        GoogleSignInHelper(context = context)
    }

    override val analyticsHelper: FirebaseAnalyticsHelper by lazy {
        FirebaseAnalyticsHelper(context = context)
    }

    // Agency Catalog Use Cases
    override val getAgencyProfileUseCase: GetAgencyProfileUseCase by lazy {
        GetAgencyProfileUseCase(repository = agencyRepository)
    }

    override val getAgencyServicesUseCase: GetAgencyServicesUseCase by lazy {
        GetAgencyServicesUseCase(repository = agencyRepository)
    }

    override val getServiceDetailsUseCase: GetServiceDetailsUseCase by lazy {
        GetServiceDetailsUseCase(repository = agencyRepository)
    }

    override val getPortfolioUseCase: GetPortfolioUseCase by lazy {
        GetPortfolioUseCase(repository = agencyRepository)
    }

    override val getPortfolioDetailsUseCase: GetPortfolioDetailsUseCase by lazy {
        GetPortfolioDetailsUseCase(repository = agencyRepository)
    }

    override val getProcessStepsUseCase: GetProcessStepsUseCase by lazy {
        GetProcessStepsUseCase(repository = agencyRepository)
    }

    override val getUserProfileUseCase: GetUserProfileUseCase by lazy {
        GetUserProfileUseCase(repository = agencyRepository)
    }

    override val submitQuoteUseCase: SubmitQuoteUseCase by lazy {
        SubmitQuoteUseCase(repository = agencyRepository)
    }

    override val submitContactUseCase: SubmitContactUseCase by lazy {
        SubmitContactUseCase(repository = agencyRepository)
    }

    // Auth Use Cases
    override val getAuthStateUseCase: GetAuthStateUseCase by lazy {
        GetAuthStateUseCase(repository = authRepository)
    }

    override val signInWithEmailUseCase: SignInWithEmailUseCase by lazy {
        SignInWithEmailUseCase(repository = authRepository)
    }

    override val signUpWithEmailUseCase: SignUpWithEmailUseCase by lazy {
        SignUpWithEmailUseCase(repository = authRepository)
    }

    override val signInWithGoogleUseCase: SignInWithGoogleUseCase by lazy {
        SignInWithGoogleUseCase(repository = authRepository)
    }

    override val sendPasswordResetUseCase: SendPasswordResetUseCase by lazy {
        SendPasswordResetUseCase(repository = authRepository)
    }

    override val signOutUseCase: SignOutUseCase by lazy {
        SignOutUseCase(repository = authRepository)
    }

    // Firestore Use Cases
    override val submitLeadUseCase: SubmitLeadUseCase by lazy {
        SubmitLeadUseCase(repository = firestoreRepository)
    }

    override val submitQuoteLeadUseCase: SubmitQuoteLeadUseCase by lazy {
        SubmitQuoteLeadUseCase(
            firestoreRepository = firestoreRepository,
            storageRepository = storageRepository,
            notificationRepository = notificationRepository,
            authRepository = authRepository,
            context = context
        )
    }

    override val getClientProjectsUseCase: GetClientProjectsUseCase by lazy {
        GetClientProjectsUseCase(repository = firestoreRepository)
    }

    override val getClientTicketsUseCase: GetClientTicketsUseCase by lazy {
        GetClientTicketsUseCase(repository = firestoreRepository)
    }

    override val submitSupportTicketUseCase: SubmitSupportTicketUseCase by lazy {
        SubmitSupportTicketUseCase(repository = firestoreRepository)
    }

    override val observeUserNotificationsUseCase: ObserveUserNotificationsUseCase by lazy {
        ObserveUserNotificationsUseCase(repository = firestoreRepository)
    }

    override val observeUserProfileUseCase: ObserveUserProfileUseCase by lazy {
        ObserveUserProfileUseCase(repository = firestoreRepository)
    }

    override val getProjectDetailsUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectDetailsUseCase by lazy {
        com.gotechmedia.app.domain.usecase.firestore.GetProjectDetailsUseCase(repository = firestoreRepository)
    }

    override val getProjectMilestonesUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectMilestonesUseCase by lazy {
        com.gotechmedia.app.domain.usecase.firestore.GetProjectMilestonesUseCase(repository = firestoreRepository)
    }

    override val getProjectDocumentsUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectDocumentsUseCase by lazy {
        com.gotechmedia.app.domain.usecase.firestore.GetProjectDocumentsUseCase(repository = firestoreRepository)
    }

    override val getProjectMessagesUseCase: com.gotechmedia.app.domain.usecase.firestore.GetProjectMessagesUseCase by lazy {
        com.gotechmedia.app.domain.usecase.firestore.GetProjectMessagesUseCase(repository = firestoreRepository)
    }

    override val sendProjectMessageUseCase: com.gotechmedia.app.domain.usecase.firestore.SendProjectMessageUseCase by lazy {
        com.gotechmedia.app.domain.usecase.firestore.SendProjectMessageUseCase(repository = firestoreRepository)
    }

    override val markNotificationAsReadUseCase: com.gotechmedia.app.domain.usecase.firestore.MarkNotificationAsReadUseCase by lazy {
        com.gotechmedia.app.domain.usecase.firestore.MarkNotificationAsReadUseCase(repository = firestoreRepository)
    }

    override val markAgencyNotificationAsReadUseCase: com.gotechmedia.app.domain.usecase.notification.MarkNotificationAsReadUseCase by lazy {
        com.gotechmedia.app.domain.usecase.notification.MarkNotificationAsReadUseCase(notificationRepository = notificationRepository)
    }

    override val markAllNotificationsAsReadUseCase: com.gotechmedia.app.domain.usecase.notification.MarkAllNotificationsAsReadUseCase by lazy {
        com.gotechmedia.app.domain.usecase.notification.MarkAllNotificationsAsReadUseCase(notificationRepository = notificationRepository)
    }

    override val deleteNotificationUseCase: com.gotechmedia.app.domain.usecase.notification.DeleteNotificationUseCase by lazy {
        com.gotechmedia.app.domain.usecase.notification.DeleteNotificationUseCase(notificationRepository = notificationRepository)
    }

    override val registerDeviceTokenUseCase: com.gotechmedia.app.domain.usecase.notification.RegisterDeviceTokenUseCase by lazy {
        com.gotechmedia.app.domain.usecase.notification.RegisterDeviceTokenUseCase(notificationRepository = notificationRepository)
    }

    override val createNotificationUseCase: com.gotechmedia.app.domain.usecase.notification.CreateNotificationUseCase by lazy {
        com.gotechmedia.app.domain.usecase.notification.CreateNotificationUseCase(notificationRepository = notificationRepository)
    }

    override val notificationCenterObserveNotificationsUseCase: com.gotechmedia.app.domain.usecase.notification.ObserveUserNotificationsUseCase by lazy {
        com.gotechmedia.app.domain.usecase.notification.ObserveUserNotificationsUseCase(notificationRepository = notificationRepository)
    }
}
