package com.example.pocketplan.ui.insights

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class InsightsUiState(
    val isLoading: Boolean = false,
    val dataPoints: List<Double> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class InsightsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()
}
