package com.example.pocketplan.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.data.repository.AuthRepository
import com.example.pocketplan.data.repository.BudgetRepository
import com.example.pocketplan.data.repository.ExpenseRepository
import com.example.pocketplan.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class InsightsUiState(
    val totalBudget: Long = 0L,
    val totalSpent: Long = 0L,
    val remainingBudget: Long = 0L,
    val goalTarget: Long = 0L,
    val goalProgress: Long = 0L,
    val freeFunds: Long = 0L,
    val daysLeft: Int = 0,
    val dailyAllowance: Long = 0L,
    val percentageUsed: Float = 0f,
    val categoryBreakdown: Map<String, Float> = emptyMap(),
    val monthlyTrend: List<Pair<String, Long>> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    // Until real auth is wired in, all data is associated with this placeholder user.
    private val currentUserId = "default_user"

    init {
        viewModelScope.launch {
            // Combining flows for the default user
            combine(
                expenseRepository.getRecentExpenses(currentUserId),
                expenseRepository.getTotalSpent(currentUserId),
                budgetRepository.getCurrentBudget(currentUserId),
                expenseRepository.getMonthlyTrend(currentUserId),
                goalRepository.getAllGoals(currentUserId)
            ) { expenses, totalSpent, budget, trends, goals ->
                DataSnapshot(expenses, totalSpent, budget, trends, goals)
            }.flatMapLatest { snapshot ->
                val budget = snapshot.budget
                if (budget != null) {
                    budgetRepository.getCategories(budget.id).map { categories ->
                        val spent = snapshot.totalSpent ?: 0L
                        val remaining = budget.totalFunds - spent

                        val goalTarget = snapshot.goals.sumOf { it.targetAmount }.toLong()
                        val goalProgress = snapshot.goals.sumOf { goal ->
                            when (goal.status) {
                                com.example.pocketplan.data.model.GoalStatus.COMPLETED -> goal.targetAmount
                                com.example.pocketplan.data.model.GoalStatus.IN_PROGRESS -> goal.targetAmount * 0.45
                                else -> 0.0
                            }
                        }.toLong()

                        // Free funds = Remaining Budget - (Future money needed for goals)
                        val futureGoalNeeds = (goalTarget - goalProgress).coerceAtLeast(0)
                        val freeFunds = (remaining - futureGoalNeeds).coerceAtLeast(0)

                        val percentage =
                            if (budget.totalFunds > 0) (spent.toFloat() / budget.totalFunds) * 100 else 0f

                        val daysLeft = calculateDaysLeft(budget)
                        val dailyAllowance = if (daysLeft > 0) freeFunds / daysLeft else 0L

                        val breakdown = snapshot.expenses.groupBy { it.categoryId }
                            .mapKeys { (catId, _) ->
                                categories.find { it.name == catId }?.name ?: "Other"
                            }
                            .mapValues { (_, categoryExpenses) ->
                                val categoryTotal = categoryExpenses.sumOf { it.amount }
                                if (spent > 0) (categoryTotal.toFloat() / spent) else 0f
                            }

                        val trendList = snapshot.trends.toList()
                            .sortedBy {
                                try {
                                    SimpleDateFormat("MMM yyyy", Locale.getDefault()).parse(it.first)?.time ?: 0L
                                } catch (e: Exception) {
                                    0L
                                }
                            }
                            .map { it.first.split(" ")[0] to it.second.toLong() }
                            .takeLast(4)

                        InsightsUiState(
                            totalBudget = budget.totalFunds,
                            totalSpent = spent,
                            remainingBudget = remaining,
                            goalTarget = goalTarget,
                            goalProgress = goalProgress,
                            freeFunds = freeFunds,
                            daysLeft = daysLeft,
                            dailyAllowance = dailyAllowance,
                            percentageUsed = percentage,
                            categoryBreakdown = breakdown,
                            monthlyTrend = trendList
                        )
                    }
                } else {
                    flowOf(InsightsUiState())
                }
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }

    private data class DataSnapshot(
        val expenses: List<com.example.pocketplan.data.model.Expense>,
        val totalSpent: Long?,
        val budget: Budget?,
        val trends: Map<String, Double>,
        val goals: List<Goal>
    )

    private fun calculateDaysLeft(budget: Budget): Int {
        val calendar = Calendar.getInstance()
        val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
        val currentYear = calendar.get(Calendar.YEAR)

        // Find the index of the current month in selectedMonths
        val currentIndex = budget.selectedMonths.indexOf(currentMonth)
        if (currentIndex == -1) return 0

        // For simplicity, assume each month has 30 days and calculate remaining days in the budget period
        val remainingMonths = budget.selectedMonths.size - 1 - currentIndex
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        return (remainingMonths * 30) + (daysInCurrentMonth - dayOfMonth)
    }
}
