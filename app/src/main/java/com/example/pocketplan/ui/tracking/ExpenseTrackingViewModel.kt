package com.example.pocketplan.ui.tracking

import androidx.lifecycle.ViewModel
import com.example.pocketplan.data.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ExpenseTrackingUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ExpenseTrackingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseTrackingUiState())
    val uiState: StateFlow<ExpenseTrackingUiState> = _uiState.asStateFlow()
}
