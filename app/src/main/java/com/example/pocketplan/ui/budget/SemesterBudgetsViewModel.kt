package com.example.pocketplan.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.Category
import com.example.pocketplan.data.model.CategoryStatus
import com.example.pocketplan.data.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class SemesterBudgetsUiState(
    val budgets: List<BudgetSummary> = emptyList(),
    val expandedBudgetIds: Set<Long> = emptySet(),
    val isCreateModalOpen: Boolean = false,
    val createName: String = "",
    val createYear: String = ""
)

data class BudgetSummary(
    val id: Long,
    val semesterName: String,
    val totalFunds: Long,
    val monthCount: Int,
    val allocationStatus: AllocationStatus,
    val createdDate: String,
    val categories: List<Category> = emptyList()
)

enum class AllocationStatus {
    FULLY_ALLOCATED,
    PARTIALLY_ALLOCATED,
    NOT_ALLOCATED
}

@HiltViewModel
class SemesterBudgetsViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _expandedBudgetIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _modalState = MutableStateFlow(Triple(false, "", "")) // isOpen, name, year

    val uiState: StateFlow<SemesterBudgetsUiState> = combine(
        repository.getAllBudgetsWithCategories(),
        _expandedBudgetIds,
        _modalState
    ) { budgetsWithCats, expandedIds, modal ->
        SemesterBudgetsUiState(
            budgets = budgetsWithCats
                .sortedByDescending { it.budget.createdDate }
                .map { it.toSummary() },
            expandedBudgetIds = expandedIds,
            isCreateModalOpen = modal.first,
            createName = modal.second,
            createYear = modal.third
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SemesterBudgetsUiState()
    )

    fun openCreateModal() {
        _modalState.update { it.copy(first = true, second = "", third = "") }
    }

    fun dismissCreateModal() {
        _modalState.update { it.copy(first = false) }
    }

    fun onNameChange(name: String) {
        _modalState.update { it.copy(second = name) }
    }

    fun onYearChange(year: String) {
        _modalState.update { it.copy(third = year) }
    }

    fun createBudget(onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val (_, name, _) = _modalState.value
            val newBudget = Budget(
                semesterName = name,
                totalFunds = 0L,
                selectedMonths = emptyList(),
                createdDate = System.currentTimeMillis()
            )
            val id = repository.insertBudget(newBudget)
            dismissCreateModal()
            onComplete(id)
        }
    }

    fun toggleExpanded(budgetId: Long) {
        _expandedBudgetIds.update { 
            if (it.contains(budgetId)) it - budgetId else it + budgetId 
        }
    }

    fun updateCategoryStatus(budgetId: Long, categoryId: Long, newStatus: CategoryStatus) {
        viewModelScope.launch {
            // We need to find the category to update it
            // In a real app, we might have a specific updateStatus DAO method
            val categories = repository.getCategories(budgetId).first()
            val category = categories.find { it.id == categoryId }
            category?.let {
                repository.updateCategory(it.copy(status = newStatus))
            }
        }
    }
}

private fun com.example.pocketplan.data.model.BudgetWithCategories.toSummary(): BudgetSummary {
    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    val totalAllocated = categories.sumOf { it.allocatedAmount }
    
    val status = when {
        budget.totalFunds > 0 && totalAllocated >= budget.totalFunds -> AllocationStatus.FULLY_ALLOCATED
        totalAllocated > 0 -> AllocationStatus.PARTIALLY_ALLOCATED
        else -> AllocationStatus.NOT_ALLOCATED
    }
    
    return BudgetSummary(
        id = budget.id,
        semesterName = budget.semesterName,
        totalFunds = budget.totalFunds,
        monthCount = budget.selectedMonths.size,
        allocationStatus = status,
        createdDate = dateFormat.format(Date(budget.createdDate)),
        categories = categories
    )
}
