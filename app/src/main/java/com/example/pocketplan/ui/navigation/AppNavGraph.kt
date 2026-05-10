package com.example.pocketplan.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.pocketplan.ui.profile.ProfileScreen
import com.example.pocketplan.ui.theme.PrimaryBlue

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val onLogout: () -> Unit = {
        authViewModel.logout()
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val state by authViewModel.uiState.collectAsState()

            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    navController.navigate(Screen.SemesterBudgets.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            if (!state.isSessionChecked) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                LoginScreen(
                    state = state,
                    onLoginClick = { email, pass -> authViewModel.login(email, pass) },
                    onRegisterClick = { navController.navigate(Screen.Register.route) },
                    onForgotPasswordClick = { email -> authViewModel.sendPasswordReset(email) }
                )
            }
        }

        composable(Screen.Register.route) {
            val state by authViewModel.uiState.collectAsState()

            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    navController.navigate(Screen.SemesterBudgets.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                state = state,
                onRegisterClick = { name, email, pass -> authViewModel.register(name, email, pass) },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SemesterBudgets.route) {
            val budgetViewModel: SemesterBudgetsViewModel = hiltViewModel()
            SemesterBudgetsScreen(
                viewModel = budgetViewModel,
                onBudgetClick = { budgetId ->
                    navController.navigate(Screen.BudgetSetup.createRoute(budgetId))
                },
                onCreateConfirmed = { budgetId ->
                    navController.navigate(Screen.BudgetSetup.createRoute(budgetId))
                },
                onLogoutClick = onLogout
            )
        }

        composable(Screen.BudgetSetup.route) { backStackEntry ->
            val viewModel: BudgetViewModel = hiltViewModel()
            val budgetId = backStackEntry.arguments?.getString("budgetId")?.toLongOrNull() ?: 0L
            LaunchedEffect(budgetId) { viewModel.loadBudget(budgetId) }
            BudgetSetupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Goals.route) {
            GoalsScreen(onLogoutClick = onLogout)
        }

        composable(Screen.Tracking.route) {
            ExpenseTrackingScreen(onLogoutClick = onLogout)
        }

        composable(Screen.Insights.route) {
            InsightsScreen(onLogoutClick = onLogout)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onLogoutClick = onLogout
            )
        }
    }
}