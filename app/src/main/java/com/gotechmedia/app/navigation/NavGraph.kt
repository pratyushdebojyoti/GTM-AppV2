package com.gotechmedia.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gotechmedia.app.core.utils.Constants
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.di.ViewModelFactoryProvider
import com.gotechmedia.app.presentation.common.AgencyBottomBar
import com.gotechmedia.app.presentation.screens.about.AboutScreen
import com.gotechmedia.app.presentation.screens.auth.LoginScreen
import com.gotechmedia.app.presentation.screens.auth.RegisterScreen
import com.gotechmedia.app.presentation.screens.contact.ContactScreen
import com.gotechmedia.app.presentation.screens.contact.ContactViewModel
import com.gotechmedia.app.presentation.screens.home.HomeScreen
import com.gotechmedia.app.presentation.screens.home.HomeViewModel
import com.gotechmedia.app.presentation.screens.more.MoreScreen
import com.gotechmedia.app.presentation.screens.onboarding.OnboardingScreen
import com.gotechmedia.app.presentation.screens.portfolio.PortfolioScreen
import com.gotechmedia.app.presentation.screens.portfolio.PortfolioViewModel
import com.gotechmedia.app.presentation.screens.portfoliodetails.PortfolioDetailsScreen
import com.gotechmedia.app.presentation.screens.portfoliodetails.PortfolioDetailsViewModel
import com.gotechmedia.app.presentation.screens.process.AgencyProcessScreen
import com.gotechmedia.app.presentation.screens.process.AgencyProcessViewModel
import com.gotechmedia.app.presentation.screens.profile.ProfileScreen
import com.gotechmedia.app.presentation.screens.quote.CustomQuoteScreen
import com.gotechmedia.app.presentation.screens.quote.CustomQuoteViewModel
import com.gotechmedia.app.presentation.screens.servicedetails.ServiceDetailsScreen
import com.gotechmedia.app.presentation.screens.servicedetails.ServiceDetailsViewModel
import com.gotechmedia.app.presentation.screens.services.ServicesScreen
import com.gotechmedia.app.presentation.screens.services.ServicesViewModel
import com.gotechmedia.app.presentation.screens.settings.SettingsScreen
import com.gotechmedia.app.presentation.screens.splash.SplashScreen
import com.gotechmedia.app.presentation.screens.testimonials.TestimonialsScreen
import com.gotechmedia.app.ui.theme.ObsidianCanvas

/**
 * Root Navigation Graph for GoTech Media.
 * Orchestrates all 16 screens of the public UI layer with Apple-inspired fluid transitions
 * and unified bottom navigation bar management.
 */
@Composable
fun GoTechNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route,
    incomingDeepLink: String? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    val container = LocalAppContainer.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    androidx.compose.runtime.LaunchedEffect(incomingDeepLink) {
        if (!incomingDeepLink.isNullOrBlank()) {
            navController.navigate(incomingDeepLink) {
                launchSingleTop = true
            }
            onDeepLinkHandled()
        }
    }

    // Show bottom navigation on primary top-level tabs
    val isBottomBarVisible = currentRoute in listOf(
        Screen.Home.route,
        Screen.Services.route,
        Screen.Portfolio.route,
        Screen.Contact.route,
        Screen.More.route
    )

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                AgencyBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetScreen ->
                        navController.navigate(targetScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isBottomBarVisible) innerPadding.calculateBottomPadding() else 0.dp),
            enterTransition = {
                fadeIn(animationSpec = tween(Constants.ANIM_DURATION_SHORT)) +
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(Constants.ANIM_DURATION_MEDIUM)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(Constants.ANIM_DURATION_SHORT)) +
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(Constants.ANIM_DURATION_MEDIUM)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(Constants.ANIM_DURATION_SHORT)) +
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(Constants.ANIM_DURATION_MEDIUM)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(Constants.ANIM_DURATION_SHORT)) +
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(Constants.ANIM_DURATION_MEDIUM)
                        )
            }
        ) {
            // 1. Splash Screen
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onNavigateNext = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onSkipToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Onboarding Screen
            composable(route = Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        container.analyticsHelper.logOnboardingCompleted()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Home Screen
            composable(route = Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = ViewModelFactoryProvider.provideHomeViewModelFactory(container)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToServices = { navController.navigate(Screen.Services.route) },
                    onNavigateToServiceDetails = { serviceId ->
                        navController.navigate(Screen.ServiceDetails.createRoute(serviceId))
                    },
                    onNavigateToPortfolio = { navController.navigate(Screen.Portfolio.route) },
                    onNavigateToPortfolioDetails = { portfolioId ->
                        navController.navigate(Screen.PortfolioDetails.createRoute(portfolioId))
                    },
                    onNavigateToQuote = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    },
                    onNavigateToProcess = { navController.navigate(Screen.AgencyProcess.route) },
                    onNavigateToContact = { navController.navigate(Screen.Contact.route) }
                )
            }

            // 4. About Screen
            composable(route = Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuote = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    },
                    onNavigateToContact = { navController.navigate(Screen.Contact.route) }
                )
            }

            // 5. Services Screen
            composable(route = Screen.Services.route) {
                val servicesViewModel: ServicesViewModel = viewModel(
                    factory = ViewModelFactoryProvider.provideServicesViewModelFactory(container)
                )
                ServicesScreen(
                    viewModel = servicesViewModel,
                    onServiceClick = { serviceId ->
                        navController.navigate(Screen.ServiceDetails.createRoute(serviceId))
                    },
                    onQuoteClick = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    }
                )
            }

            // 6. Service Details Screen
            composable(
                route = Screen.ServiceDetails.route,
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                androidx.compose.runtime.LaunchedEffect(serviceId) {
                    if (serviceId.isNotBlank()) {
                        container.analyticsHelper.logServiceView(serviceId = serviceId)
                    }
                }
                val detailsViewModel: ServiceDetailsViewModel = viewModel(
                    factory = ViewModelFactoryProvider.provideServiceDetailsViewModelFactory(serviceId, container)
                )
                ServiceDetailsScreen(
                    viewModel = detailsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuote = { id ->
                        navController.navigate(Screen.CustomQuote.createRoute(id))
                    }
                )
            }

            // 7. Portfolio Screen
            composable(route = Screen.Portfolio.route) {
                val portfolioViewModel: PortfolioViewModel = viewModel(
                    factory = ViewModelFactoryProvider.providePortfolioViewModelFactory(container)
                )
                PortfolioScreen(
                    viewModel = portfolioViewModel,
                    onItemClick = { portfolioId ->
                        navController.navigate(Screen.PortfolioDetails.createRoute(portfolioId))
                    },
                    onQuoteClick = {
                        navController.navigate(Screen.CustomQuote.createRoute())
                    }
                )
            }

            // 8. Portfolio Details Screen
            composable(
                route = Screen.PortfolioDetails.route,
                arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
            ) { backStackEntry ->
                val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: ""
                androidx.compose.runtime.LaunchedEffect(portfolioId) {
                    if (portfolioId.isNotBlank()) {
                        container.analyticsHelper.logPortfolioView(portfolioId = portfolioId)
                    }
                }
                val detailsViewModel: PortfolioDetailsViewModel = viewModel(
                    factory = ViewModelFactoryProvider.providePortfolioDetailsViewModelFactory(portfolioId, container)
                )
                PortfolioDetailsScreen(
                    viewModel = detailsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuote = {
                        navController.navigate(Screen.CustomQuote.createRoute())
                    }
                )
            }

            // 9. Custom Quote Screen
            composable(
                route = Screen.CustomQuote.route,
                arguments = listOf(
                    navArgument("serviceId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                val quoteViewModel: CustomQuoteViewModel = viewModel(
                    factory = ViewModelFactoryProvider.provideCustomQuoteViewModelFactory(serviceId, container)
                )
                CustomQuoteScreen(
                    viewModel = quoteViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 10. Contact Screen
            composable(route = Screen.Contact.route) {
                val contactViewModel: ContactViewModel = viewModel(
                    factory = ViewModelFactoryProvider.provideContactViewModelFactory(container)
                )
                ContactScreen(
                    viewModel = contactViewModel,
                    onNavigateToQuote = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    }
                )
            }

            // 11. Agency Process Screen
            composable(route = Screen.AgencyProcess.route) {
                val processViewModel: AgencyProcessViewModel = viewModel(
                    factory = ViewModelFactoryProvider.provideAgencyProcessViewModelFactory(container)
                )
                AgencyProcessScreen(
                    viewModel = processViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuote = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    }
                )
            }

            // 12. Testimonials Screen
            composable(route = Screen.Testimonials.route) {
                TestimonialsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToContact = { navController.navigate(Screen.Contact.route) },
                    onNavigateToQuote = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    }
                )
            }

            // 13. Login Screen
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.ClientDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 14. Register Screen
            composable(route = Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onRegisterSuccess = {
                        navController.navigate(Screen.ClientDashboard.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            // 15. Profile Screen / Client Portal
            composable(route = Screen.Profile.route) {
                com.gotechmedia.app.presentation.screens.client.ClientDashboardScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProjects = { navController.navigate(Screen.ClientProjects.route) },
                    onNavigateToProjectDetails = { projectId ->
                        navController.navigate(Screen.ProjectDetails.createRoute(projectId))
                    },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onSignOut = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // 15B. Dedicated Client Dashboard Screen (Login -> Client Dashboard)
            composable(route = Screen.ClientDashboard.route) {
                com.gotechmedia.app.presentation.screens.client.ClientDashboardScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProjects = { navController.navigate(Screen.ClientProjects.route) },
                    onNavigateToProjectDetails = { projectId ->
                        navController.navigate(Screen.ProjectDetails.createRoute(projectId))
                    },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onSignOut = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // 15C. Client Projects Screen (Projects Index)
            composable(route = Screen.ClientProjects.route) {
                com.gotechmedia.app.presentation.screens.client.ClientProjectsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProjectDetails = { projectId ->
                        navController.navigate(Screen.ProjectDetails.createRoute(projectId))
                    }
                )
            }

            // 15D. Project Details Screen (Deep engagement: overview, milestones, team, docs, messages, support tickets)
            composable(
                route = Screen.ProjectDetails.route,
                arguments = listOf(
                    androidx.navigation.navArgument("projectId") {
                        type = androidx.navigation.NavType.StringType
                        defaultValue = ""
                    },
                    androidx.navigation.navArgument("tab") {
                        type = androidx.navigation.NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId").orEmpty()
                val tabIndex = backStackEntry.arguments?.getInt("tab") ?: 0
                com.gotechmedia.app.presentation.screens.projectdetails.ProjectDetailsScreen(
                    projectId = projectId,
                    initialTabIndex = tabIndex,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // In-App Notification Center
            composable(route = Screen.Notifications.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val notificationsViewModel: com.gotechmedia.app.presentation.screens.notifications.NotificationsViewModel =
                    viewModel(factory = ViewModelFactoryProvider.provideNotificationsViewModelFactory(context, container))
                com.gotechmedia.app.presentation.screens.notifications.NotificationsScreen(
                    viewModel = notificationsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateDeepLink = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 16. Settings Screen
            composable(route = Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // More Hub Screen (Tab 5)
            composable(route = Screen.More.route) {
                MoreScreen(
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onNavigateToProcess = { navController.navigate(Screen.AgencyProcess.route) },
                    onNavigateToTestimonials = { navController.navigate(Screen.Testimonials.route) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToQuote = { serviceId ->
                        navController.navigate(Screen.CustomQuote.createRoute(serviceId))
                    }
                )
            }
        }
    }
}
