package com.example.pocketplan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pocketplan.ui.auth.LoginScreen
import com.example.pocketplan.ui.auth.RegisterScreen
import com.example.pocketplan.ui.budget.BudgetSetupScreen
import com.example.pocketplan.ui.goals.GoalsScreen
import com.example.pocketplan.ui.tracking.ExpenseTrackingScreen
import com.example.pocketplan.ui.insights.InsightsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { navController.navigate(Screen.BudgetSetup.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { navController.navigate(Screen.Login.route) },
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable(Screen.BudgetSetup.route) {
            BudgetSetupScreen()
        }
        composable(Screen.Goals.route) {
            GoalsScreen()
        }
        composable(Screen.Tracking.route) {
            ExpenseTrackingScreen()
        }
        composable(Screen.Insights.route) {
            InsightsScreen()
        }
    }
}
