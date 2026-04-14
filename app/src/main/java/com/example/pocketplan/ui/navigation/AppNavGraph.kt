package com.example.pocketplan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.pocketplan.ui.auth.LoginScreen
import com.example.pocketplan.ui.auth.RegisterScreen
import com.example.pocketplan.ui.budget.BudgetSetupScreen
import com.example.pocketplan.ui.budget.BudgetViewModel
import com.example.pocketplan.ui.budget.SemesterBudgetsScreen
import com.example.pocketplan.ui.budget.SemesterBudgetsViewModel
import com.example.pocketplan.ui.goals.GoalsScreen
import com.example.pocketplan.ui.tracking.ExpenseTrackingScreen
import com.example.pocketplan.ui.insights.InsightsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.SemesterBudgets.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Screen.SemesterBudgets.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable(Screen.SemesterBudgets.route) {
            val viewModel: SemesterBudgetsViewModel = viewModel()
            SemesterBudgetsScreen(
                viewModel = viewModel,
                onBudgetClick = { budgetId ->
                    navController.navigate(Screen.BudgetSetup.createRoute(budgetId))
                },
                onCreateConfirmed = { budgetId ->
                    navController.navigate(Screen.BudgetSetup.createRoute(budgetId))
                }
            )
        }
        composable(
            route = Screen.BudgetSetup.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
            val budgetViewModel: BudgetViewModel = viewModel()

            LaunchedEffect(budgetId) {
                budgetViewModel.loadBudget(budgetId)
            }

            BudgetSetupScreen(
                viewModel = budgetViewModel,
                onBack = { navController.popBackStack() }
            )
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
