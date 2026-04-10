package com.example.pocketplan.ui.budget

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.pocketplan.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class BudgetUiState(
    val id: String = "",
    val semesterName: String = "",
    val totalFunds: Long = 2_500_000L,
    val selectedMonths: List<String> = listOf("Jan", "Feb", "Mar", "Apr", "May"),
    val categories: List<Category> = listOf(
        Category("1", "Rent", 800_000.0, 32.0),
        Category("2", "Tuition", 1_000_000.0, 40.0)
    ),
    val isEditing: Boolean = false,
    val attachedImageUri: Uri? = null
)

class BudgetViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    fun loadBudget(budgetId: String) {
        // In a real app, fetch from repository. For now, just set the ID.
        _uiState.update { it.copy(id = budgetId, semesterName = "Loading budget $budgetId...") }
    }

    fun updateTotalFunds(amount: Long) {
        if (!_uiState.value.isEditing) return
        _uiState.update { it.copy(totalFunds = amount) }
        recalculateCategoryAmounts()
    }

    fun toggleMonth(month: String) {
        if (!_uiState.value.isEditing) return
        _uiState.update { state ->
            val newMonths = if (state.selectedMonths.contains(month)) {
                state.selectedMonths.filter { it != month }
            } else {
                state.selectedMonths + month
            }
            state.copy(selectedMonths = newMonths)
        }
    }

    fun addCategory(name: String, percentage: Int) {
        if (!_uiState.value.isEditing) return
        _uiState.update { state ->
            val newCategory = Category(
                id = UUID.randomUUID().toString(),
                name = name,
                allocatedAmount = (state.totalFunds * percentage / 100.0),
                percentage = percentage.toDouble()
            )
            state.copy(categories = state.categories + newCategory)
        }
    }

    fun updateCategoryPercentage(categoryId: String, percentage: Int) {
        if (!_uiState.value.isEditing) return
        _uiState.update { state ->
            val updatedCategories = state.categories.map { category ->
                if (category.id == categoryId) {
                    category.copy(
                        percentage = percentage.toDouble(),
                        allocatedAmount = (state.totalFunds * percentage / 100.0)
                    )
                } else {
                    category
                }
            }
            state.copy(categories = updatedCategories)
        }
    }

    fun updateCategoryAmount(categoryId: String, amount: Double) {
        if (!_uiState.value.isEditing) return
        _uiState.update { state ->
            val updatedCategories = state.categories.map { category ->
                if (category.id == categoryId) {
                    val percentage = if (state.totalFunds > 0) (amount / state.totalFunds) * 100.0 else 0.0
                    category.copy(
                        allocatedAmount = amount,
                        percentage = percentage
                    )
                } else {
                    category
                }
            }
            state.copy(categories = updatedCategories)
        }
    }

    private fun recalculateCategoryAmounts() {
        _uiState.update { state ->
            val updatedCategories = state.categories.map { category ->
                category.copy(allocatedAmount = (state.totalFunds * category.percentage / 100.0))
            }
            state.copy(categories = updatedCategories)
        }
    }

    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun saveBudget() {
        // Mock save
        _uiState.update { it.copy(isEditing = false) }
    }
}
