package com.example.pocketplan.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object BudgetSetup : Screen("budget_setup")
    object Goals : Screen("goals")
    object Tracking : Screen("tracking")
    object Insights : Screen("insights")
}
