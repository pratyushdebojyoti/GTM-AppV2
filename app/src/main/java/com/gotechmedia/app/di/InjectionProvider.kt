package com.gotechmedia.app.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gotechmedia.app.presentation.foundation.FoundationViewModel
import com.gotechmedia.app.presentation.screens.contact.ContactViewModel
import com.gotechmedia.app.presentation.screens.home.HomeViewModel
import com.gotechmedia.app.presentation.screens.portfolio.PortfolioViewModel
import com.gotechmedia.app.presentation.screens.portfoliodetails.PortfolioDetailsViewModel
import com.gotechmedia.app.presentation.screens.process.AgencyProcessViewModel
import com.gotechmedia.app.presentation.screens.quote.CustomQuoteViewModel
import com.gotechmedia.app.presentation.screens.servicedetails.ServiceDetailsViewModel
import com.gotechmedia.app.presentation.screens.services.ServicesViewModel

/**
 * CompositionLocal providing access to the dependency injection container across Compose trees.
 */
val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("AppContainer has not been initialized in LocalAppContainer")
}

/**
 * Convenience wrapper composable to provide dependencies down the composition hierarchy.
 */
@Composable
fun ProvideAppContainer(
    container: AppContainer,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppContainer provides container) {
        content()
    }
}

/**
 * ViewModel Factory Provider for the Clean Architecture layer.
 * Creates ViewModels with constructor-injected use cases from the AppContainer.
 */
object ViewModelFactoryProvider {

    fun provideFoundationViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                FoundationViewModel(
                    getAgencyProfileUseCase = container.getAgencyProfileUseCase,
                    getAgencyServicesUseCase = container.getAgencyServicesUseCase
                )
            }
        }

    fun provideHomeViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                HomeViewModel(
                    getAgencyProfileUseCase = container.getAgencyProfileUseCase,
                    getAgencyServicesUseCase = container.getAgencyServicesUseCase,
                    getPortfolioUseCase = container.getPortfolioUseCase
                )
            }
        }

    fun provideServicesViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                ServicesViewModel(
                    getAgencyServicesUseCase = container.getAgencyServicesUseCase
                )
            }
        }

    fun provideServiceDetailsViewModelFactory(
        serviceId: String,
        container: AppContainer
    ): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                ServiceDetailsViewModel(
                    serviceId = serviceId,
                    getServiceDetailsUseCase = container.getServiceDetailsUseCase
                )
            }
        }

    fun providePortfolioViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                PortfolioViewModel(
                    getPortfolioUseCase = container.getPortfolioUseCase
                )
            }
        }

    fun providePortfolioDetailsViewModelFactory(
        portfolioId: String,
        container: AppContainer
    ): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                PortfolioDetailsViewModel(
                    portfolioId = portfolioId,
                    getPortfolioDetailsUseCase = container.getPortfolioDetailsUseCase
                )
            }
        }

    fun provideCustomQuoteViewModelFactory(
        initialServiceId: String,
        container: AppContainer
    ): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                CustomQuoteViewModel(
                    initialServiceId = initialServiceId,
                    getAgencyServicesUseCase = container.getAgencyServicesUseCase,
                    submitQuoteLeadUseCase = container.submitQuoteLeadUseCase,
                    authRepository = container.authRepository,
                    analyticsHelper = container.analyticsHelper
                )
            }
        }

    fun provideContactViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                ContactViewModel(
                    submitContactUseCase = container.submitContactUseCase
                )
            }
        }

    fun provideAgencyProcessViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                AgencyProcessViewModel(
                    getProcessStepsUseCase = container.getProcessStepsUseCase
                )
            }
        }

    fun provideLoginViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                com.gotechmedia.app.presentation.screens.auth.LoginViewModel(
                    signInWithEmailUseCase = container.signInWithEmailUseCase,
                    signInWithGoogleUseCase = container.signInWithGoogleUseCase,
                    sendPasswordResetUseCase = container.sendPasswordResetUseCase,
                    getAuthStateUseCase = container.getAuthStateUseCase
                )
            }
        }

    fun provideRegisterViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                com.gotechmedia.app.presentation.screens.auth.RegisterViewModel(
                    signUpWithEmailUseCase = container.signUpWithEmailUseCase,
                    signInWithGoogleUseCase = container.signInWithGoogleUseCase,
                    getAuthStateUseCase = container.getAuthStateUseCase
                )
            }
        }

    fun provideProfileViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                com.gotechmedia.app.presentation.screens.profile.ProfileViewModel(
                    getAuthStateUseCase = container.getAuthStateUseCase,
                    signOutUseCase = container.signOutUseCase,
                    getClientProjectsUseCase = container.getClientProjectsUseCase,
                    getClientTicketsUseCase = container.getClientTicketsUseCase,
                    submitSupportTicketUseCase = container.submitSupportTicketUseCase
                )
            }
        }

    fun provideClientDashboardViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                com.gotechmedia.app.presentation.screens.client.ClientDashboardViewModel(
                    getAuthStateUseCase = container.getAuthStateUseCase,
                    signOutUseCase = container.signOutUseCase,
                    getClientProjectsUseCase = container.getClientProjectsUseCase,
                    observeUserNotificationsUseCase = container.observeUserNotificationsUseCase,
                    markNotificationAsReadUseCase = container.markNotificationAsReadUseCase
                )
            }
        }

    fun provideProjectDetailsViewModelFactory(projectId: String, container: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                com.gotechmedia.app.presentation.screens.projectdetails.ProjectDetailsViewModel(
                    projectId = projectId,
                    getAuthStateUseCase = container.getAuthStateUseCase,
                    getProjectDetailsUseCase = container.getProjectDetailsUseCase,
                    getProjectMilestonesUseCase = container.getProjectMilestonesUseCase,
                    getProjectDocumentsUseCase = container.getProjectDocumentsUseCase,
                    getProjectMessagesUseCase = container.getProjectMessagesUseCase,
                    sendProjectMessageUseCase = container.sendProjectMessageUseCase,
                    getClientTicketsUseCase = container.getClientTicketsUseCase,
                    submitSupportTicketUseCase = container.submitSupportTicketUseCase
                )
            }
        }

    fun provideNotificationsViewModelFactory(
        context: android.content.Context,
        container: AppContainer
    ): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                com.gotechmedia.app.presentation.screens.notifications.NotificationsViewModel(
                    observeUserNotificationsUseCase = container.notificationCenterObserveNotificationsUseCase,
                    markNotificationAsReadUseCase = container.markAgencyNotificationAsReadUseCase,
                    markAllNotificationsAsReadUseCase = container.markAllNotificationsAsReadUseCase,
                    deleteNotificationUseCase = container.deleteNotificationUseCase,
                    authRepository = container.authRepository,
                    appContext = context.applicationContext
                )
            }
        }
}

