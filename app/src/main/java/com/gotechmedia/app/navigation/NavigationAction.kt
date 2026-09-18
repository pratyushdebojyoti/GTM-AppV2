package com.gotechmedia.app.navigation

import androidx.navigation.NavHostController

/**
 * Encapsulates navigation intent and decoupling navigation actions from individual UI composables.
 */
class NavigationActions(private val navController: NavHostController) {

    fun navigateTo(screen: Screen) {
        navController.navigate(screen.route) {
            popUpTo(Screen.Foundation.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}
