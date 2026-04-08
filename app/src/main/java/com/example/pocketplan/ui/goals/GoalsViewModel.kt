package com.example.pocketplan.ui.goals

import androidx.lifecycle.ViewModel
import com.example.pocketplan.data.model.Goal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class GoalsUiState(
    val isLoading: Boolean = false,
    val goals: List<Goal> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class GoalsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
}
