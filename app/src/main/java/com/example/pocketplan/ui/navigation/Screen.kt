package com.example.pocketplan.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object SemesterBudgets : Screen("semester_budgets")
    object BudgetSetup : Screen("budget_setup/{budgetId}") {
        fun createRoute(budgetId: Any) = "budget_setup/$budgetId"
    }
    object Goals : Screen("goals")
    object Tracking : Screen("tracking")
    object Insights : Screen("insights")
}
