package com.example.pocketplan.ui.tracking

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Expense(
    val name: String,
    val category: String,
    val amount: Double,
    val date: String
)

data class TrackingUiState(
    val amount: String = "",
    val selectedCategory: String = "",
    val note: String = "",
    val date: String = "",
    val receiptImageUri: Uri? = null,
    val recentExpenses: List<Expense> = emptyList()
)

class ExpenseTrackingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amount = value)
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun onDateChange(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun saveExpense() {
        val current = _uiState.value

        if (current.amount.isBlank() || current.selectedCategory.isBlank()) return

        val newExpense = Expense(
            name = current.note.ifBlank { "Expense" },
            category = current.selectedCategory,
            amount = current.amount.toDoubleOrNull() ?: 0.0,
            date = current.date
        )

        _uiState.value = current.copy(
            amount = "",
            selectedCategory = "",
            note = "",
            date = "",
            recentExpenses = listOf(newExpense) + current.recentExpenses
        )
    }
}