package com.example.pocketplan.ui.tracking

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketplan.data.model.Category
import com.example.pocketplan.data.repository.BudgetRepository
import com.example.pocketplan.data.repository.ExpenseRepository
import com.example.pocketplan.utils.DateUtils
import com.example.pocketplan.utils.ImageStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.example.pocketplan.data.model.Expense as DomainExpense

/**
 * UI representation of a single expense row (distinct from the persisted domain model).
 */
data class ExpenseDisplay(
    val id: String,
    val name: String,
    val category: String,
    val amount: Double,
    val date: String,
    val dateMillis: Long
)

// Back-compat alias: existing composables reference `Expense` as the UI row type.
typealias Expense = ExpenseDisplay

data class TrackingUiState(
    val editingExpenseId: String? = null,
    val amount: String = "",
    val selectedCategory: Category? = null,
    val note: String = "",
    val date: String = "",
    val pickedDateMillis: Long? = null,
    val recentExpenses: List<ExpenseDisplay> = emptyList(),
    val categories: List<Category> = emptyList(),
    // Per-field validation messages. Null means the field is currently valid.
    val amountError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpenseTrackingViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    // Until real auth is wired in, all expenses are associated with this placeholder user.
    private val currentUserId = "default_user"

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    private var currentBudgetId: Long? = null

    init {
        // Observe the current budget to get categories
        viewModelScope.launch {
            budgetRepository.getCurrentBudget(currentUserId)
                .flatMapLatest { budget ->
                    currentBudgetId = budget?.id
                    if (budget != null) {
                        budgetRepository.getCategoriesByType(budget.id, false)
                    } else {
                        flowOf(emptyList())
                    }
                }
                .collect { categoryList ->
                    _uiState.update { it.copy(categories = categoryList) }
                }
        }

        // Observe the DAO flow. Any insert automatically flows back to the UI.
        viewModelScope.launch {
            expenseRepository.getRecentExpenses(currentUserId).collect { domainList ->
                val displayList = domainList
                    .map { it.toDisplay() }
                _uiState.update { it.copy(recentExpenses = displayList) }
            }
        }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value, amountError = null) }
    }

    fun onCategorySelected(category: Category) {
        _uiState.update { it.copy(selectedCategory = category, categoryError = null) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun addCategory(name: String) {
        val budgetId = currentBudgetId ?: return
        viewModelScope.launch {
            val newCategory = Category(
                budgetId = budgetId,
                name = name,
                allocatedAmount = 0, // Default for ad-hoc categories
                percentage = 0.0,
                isBudgetCategory = false
            )
            budgetRepository.insertCategory(newCategory)
        }
    }

    fun onDatePicked(millis: Long) {
        _uiState.update {
            it.copy(
                pickedDateMillis = millis,
                date = DateUtils.formatLongToDate(millis),
                dateError = null
            )
        }
    }

    fun onExpenseClick(expense: ExpenseDisplay) {
        _uiState.update {
            it.copy(
                editingExpenseId = expense.id,
                amount = expense.amount.toString(),
                selectedCategory = it.categories.find { cat -> cat.name == expense.category },
                note = expense.name,
                date = expense.date,
                pickedDateMillis = expense.dateMillis,
                amountError = null,
                categoryError = null,
                dateError = null
            )
        }
    }

    fun deleteExpense() {
        val expenseId = _uiState.value.editingExpenseId ?: return
        viewModelScope.launch {
            // We need a full Expense object to delete if the repository requires it, 
            // or just the ID if we add a deleteById method.
            // For now, let's assume we can construct a partial one or the repo has delete.
            // Let's check ExpenseRepository again.
            val domainList = expenseRepository.getRecentExpenses(currentUserId).first()
            val toDelete = domainList.find { it.id == expenseId }
            if (toDelete != null) {
                expenseRepository.deleteExpense(toDelete)
            }
            resetForm()
        }
    }

    fun saveExpense(): Boolean {
        val current = _uiState.value

        // Validate each required field. Note is optional.
        val parsedAmount = current.amount.toDoubleOrNull()
        val amountError = when {
            current.amount.isBlank() -> "Amount is required"
            parsedAmount == null -> "Enter a valid amount"
            parsedAmount <= 0.0 -> "Amount must be greater than zero"
            else -> null
        }
        val categoryError = if (current.selectedCategory == null) "Select a category" else null
        val dateError = if (current.pickedDateMillis == null) "Select a date" else null

        if (amountError != null || categoryError != null || dateError != null) {
            _uiState.update {
                it.copy(
                    amountError = amountError,
                    categoryError = categoryError,
                    dateError = dateError
                )
            }
            return false
        }

        viewModelScope.launch {
            val expense = DomainExpense(
                id = current.editingExpenseId ?: UUID.randomUUID().toString(),
                userId = currentUserId,
                amount = parsedAmount!!,
                categoryId = current.selectedCategory?.name ?: "Uncategorized",
                note = current.note,
                date = current.pickedDateMillis!!
            )

            expenseRepository.insertExpense(expense)
            
            // Clear the form after successful save
            resetForm()
        }
        return true
    }

    fun resetForm() {
        _uiState.update {
            it.copy(
                editingExpenseId = null,
                amount = "",
                selectedCategory = null,
                note = "",
                date = "",
                pickedDateMillis = null,
                amountError = null,
                categoryError = null,
                dateError = null
            )
        }
    }

    private fun DomainExpense.toDisplay(): ExpenseDisplay = ExpenseDisplay(
        id = id,
        name = note.ifBlank { "Expense" },
        category = categoryId,
        amount = amount,
        date = DateUtils.formatLongToDate(date),
        dateMillis = date
    )
}
