package com.tapcard.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tapcard.app.ui.screens.DashboardScreen
import com.tapcard.app.ui.screens.OnboardingScreen
import com.tapcard.app.ui.screens.EditorScreen
import com.tapcard.app.viewmodel.ProfileViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Shared ViewModel tied to the Navigation graph's lifecycle
    val sharedViewModel: ProfileViewModel = viewModel()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(
                viewModel = sharedViewModel,
                onComplete = { navController.navigate("dashboard") { popUpTo("onboarding") { inclusive = true } } }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = sharedViewModel,
                onEditCard = { navController.navigate("editor") }
            )
        }
        composable("editor") {
            EditorScreen(
                viewModel = sharedViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
