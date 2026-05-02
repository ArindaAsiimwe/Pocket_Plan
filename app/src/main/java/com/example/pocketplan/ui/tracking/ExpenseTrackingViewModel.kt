package com.example.pocketplan.ui.tracking

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketplan.data.repository.ExpenseRepository
import com.example.pocketplan.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.example.pocketplan.data.model.Expense as DomainExpense

/**
 * UI representation of a single expense row (distinct from the persisted domain model).
 */
data class ExpenseDisplay(
    val name: String,
    val category: String,
    val amount: Double,
    val date: String
)

// Back-compat alias: existing composables reference `Expense` as the UI row type.
typealias Expense = ExpenseDisplay

data class TrackingUiState(
    val amount: String = "",
    val selectedCategory: String = "",
    val note: String = "",
    val date: String = "",
    val pickedDateMillis: Long? = null,
    val receiptImageUri: Uri? = null,
    val recentExpenses: List<ExpenseDisplay> = emptyList(),
    val categories: List<String> = listOf("Food", "Transport", "Shopping", "Misc"),
    // Per-field validation messages. Null means the field is currently valid.
    val amountError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null
)

@HiltViewModel
class ExpenseTrackingViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    // Until real auth is wired in, all expenses are associated with this placeholder user.
    private val currentUserId = "default-user"

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    init {
        // Observe the DAO flow. Any insert automatically flows back to the UI.
        viewModelScope.launch {
            expenseRepository.getExpenses(currentUserId).collect { domainList ->
                val displayList = domainList
                    .sortedByDescending { it.date }
                    .map { it.toDisplay() }
                _uiState.update { it.copy(recentExpenses = displayList) }
            }
        }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value, amountError = null) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category, categoryError = null) }
    }

    fun addCategory(name: String) {
        _uiState.update {
            it.copy(
                categories = it.categories + name,
                selectedCategory = name,
                categoryError = null
            )
        }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
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
        val categoryError = if (current.selectedCategory.isBlank()) "Select a category" else null
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

        val expense = DomainExpense(
            id = UUID.randomUUID().toString(),
            userId = currentUserId,
            amount = parsedAmount!!,
            categoryId = current.selectedCategory, // use category name as id until a category master exists
            note = current.note,
            date = current.pickedDateMillis!!
        )

        // Clear the form immediately; the DAO flow will deliver the new list.
        _uiState.update {
            it.copy(
                amount = "",
                selectedCategory = "",
                note = "",
                date = "",
                pickedDateMillis = null,
                amountError = null,
                categoryError = null,
                dateError = null
            )
        }

        viewModelScope.launch {
            expenseRepository.addExpense(expense)
        }
        return true
    }

    fun resetForm() {
        _uiState.update {
            it.copy(
                amount = "",
                selectedCategory = "",
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
        name = note.ifBlank { "Expense" },
        category = categoryId,
        amount = amount,
        date = DateUtils.formatLongToDate(date)
    )
}