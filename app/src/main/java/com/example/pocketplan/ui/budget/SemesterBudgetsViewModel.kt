package com.example.pocketplan.ui.budget

import androidx.lifecycle.ViewModel
import com.example.pocketplan.data.model.Category
import com.example.pocketplan.data.model.CategoryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class SemesterBudgetsUiState(
    val budgets: List<BudgetSummary> = listOf(
        BudgetSummary(
            id = "1",
            semesterName = "Year 1, Semester 1 (2026)",
            totalFunds = 2_500_000L,
            monthCount = 5,
            isFullyAllocated = true,
            createdDate = "Mar 2026",
            categories = listOf(
                Category("1", "Rent", 800_000, 32, "home", CategoryStatus.PENDING),
                Category("2", "Tuition", 1_000_000, 40, "school", CategoryStatus.IN_PROGRESS)
            )
        )
    ),
    val expandedBudgetIds: Set<String> = emptySet(),
    val isCreateModalOpen: Boolean = false,
    val createName: String = "",
    val createYear: String = ""
)

data class BudgetSummary(
    val id: String,
    val semesterName: String,
    val totalFunds: Long,
    val monthCount: Int,
    val isFullyAllocated: Boolean,
    val createdDate: String,
    val categories: List<Category> = emptyList()
)

class SemesterBudgetsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SemesterBudgetsUiState())
    val uiState: StateFlow<SemesterBudgetsUiState> = _uiState.asStateFlow()

    fun openCreateModal() {
        _uiState.update { it.copy(isCreateModalOpen = true, createName = "", createYear = "") }
    }

    fun dismissCreateModal() {
        _uiState.update { it.copy(isCreateModalOpen = false) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(createName = name) }
    }

    fun onYearChange(year: String) {
        _uiState.update { it.copy(createYear = year) }
    }

    fun createBudget(): String {
        val newId = UUID.randomUUID().toString()
        val currentState = _uiState.value
        val newBudget = BudgetSummary(
            id = newId,
            semesterName = currentState.createName,
            totalFunds = 0L,
            monthCount = 0,
            isFullyAllocated = false,
            createdDate = "Mar 2026", // Simplified for now
            categories = emptyList()
        )
        _uiState.update { it.copy(
            budgets = it.budgets + newBudget,
            isCreateModalOpen = false
        ) }
        return newId
    }

    fun toggleExpanded(budgetId: String) {
        _uiState.update { state ->
            val newExpanded = if (state.expandedBudgetIds.contains(budgetId)) {
                state.expandedBudgetIds - budgetId
            } else {
                state.expandedBudgetIds + budgetId
            }
            state.copy(expandedBudgetIds = newExpanded)
        }
    }

    fun updateCategoryStatus(budgetId: String, categoryId: String, newStatus: CategoryStatus) {
        _uiState.update { state ->
            val updatedBudgets = state.budgets.map { budget ->
                if (budget.id == budgetId) {
                    val updatedCategories = budget.categories.map { category ->
                        if (category.id == categoryId) {
                            category.copy(status = newStatus)
                        } else {
                            category
                        }
                    }
                    budget.copy(categories = updatedCategories)
                } else {
                    budget
                }
            }
            state.copy(budgets = updatedBudgets)
        }
    }
}
