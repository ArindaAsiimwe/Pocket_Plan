package com.example.pocketplan.ui.budget

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class BudgetUiState(
    val isLoading: Boolean = false,
    val totalAmount: Double = 0.0
)

@HiltViewModel
class BudgetViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()
}
