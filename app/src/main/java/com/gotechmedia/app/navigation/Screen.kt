package com.gotechmedia.app.navigation

/**
 * Type-safe Screen destinations across the GoTech Media application.
 * All 16 public layer destinations + bottom navigation items.
 */
sealed class Screen(val route: String, val title: String) {
    // Top-Level Flows
    data object Splash : Screen("route_splash", "GoTech Media")
    data object Onboarding : Screen("route_onboarding", "Get Started")

    // Bottom Navigation Destinations
    data object Home : Screen("route_home", "Home")
    data object Services : Screen("route_services", "Services")
    data object Portfolio : Screen("route_portfolio", "Portfolio")
    data object Contact : Screen("route_contact", "Contact")
    data object More : Screen("route_more", "More")

    // Deep-Dive & Action Screens
    data object About : Screen("route_about", "About Us")
    data object ServiceDetails : Screen("route_service_details/{serviceId}", "Service Details") {
        fun createRoute(serviceId: String): String = "route_service_details/$serviceId"
    }
    data object PortfolioDetails : Screen("route_portfolio_details/{portfolioId}", "Case Study") {
        fun createRoute(portfolioId: String): String = "route_portfolio_details/$portfolioId"
    }
    data object CustomQuote : Screen("route_custom_quote?serviceId={serviceId}", "Get a Custom Quote") {
        fun createRoute(serviceId: String = ""): String =
            if (serviceId.isNotBlank()) "route_custom_quote?serviceId=$serviceId" else "route_custom_quote"
    }
    data object AgencyProcess : Screen("route_agency_process", "Agency Process")
    data object Testimonials : Screen("route_testimonials", "Client Reviews")
    data object Login : Screen("route_login", "Sign In")
    data object Register : Screen("route_register", "Register")
    data object Profile : Screen("route_profile", "Profile")
    data object Settings : Screen("route_settings", "Settings")

    // Authenticated Client Portal Flow
    data object ClientDashboard : Screen("route_client_dashboard", "Client Dashboard")
    data object ClientProjects : Screen("route_client_projects", "Projects")
    data object ProjectDetails : Screen("route_project_details/{projectId}?tab={tab}", "Project Details") {
        fun createRoute(projectId: String, tab: Int = 0): String = "route_project_details/$projectId?tab=$tab"
    }

    // In-App Notification Center
    data object Notifications : Screen("route_notifications", "Notifications")

    // Architecture & Foundation Review
    data object Foundation : Screen("route_foundation", "System Architecture")

    companion object {
        val bottomNavItems = listOf(
            Home,
            Services,
            Portfolio,
            Contact,
            More
        )
    }
}
