package com.example.pocketplan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pocketplan.ui.auth.AuthViewModel
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
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            
            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    navController.navigate(Screen.SemesterBudgets.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                state = state,
                onLoginClick = { email, pass -> viewModel.login(email, pass) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    navController.navigate(Screen.SemesterBudgets.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                state = state,
                onRegisterClick = { name, email, pass -> viewModel.register(name, email, pass) },
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable(Screen.SemesterBudgets.route) {
            val viewModel: SemesterBudgetsViewModel = hiltViewModel()
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
        composable(Screen.BudgetSetup.route) { backStackEntry ->
            val viewModel: BudgetViewModel = hiltViewModel()
            val budgetId = backStackEntry.arguments?.getString("budgetId") ?: "new"
            
            LaunchedEffect(budgetId) {
                viewModel.loadBudget(budgetId)
            }

            BudgetSetupScreen(
                viewModel = viewModel,
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
