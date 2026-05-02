package com.example.pocketplan.ui.insights

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class InsightsUiState(
    val totalSpent: Long = 0L,
    val remaining: Long = 0L,
    val daysLeft: Int = 0,
    val percentageUsed: Float = 0f,
    val categoryBreakdown: Map<String, Float> = emptyMap(),
    val monthlyTrend: List<Pair<String, Long>> = emptyList()
)

@HiltViewModel
class InsightsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        // Mock data for the insights screen
        _uiState.update {
            it.copy(
                totalSpent = 1_850_000L,
                remaining = 650_000L,
                daysLeft = 12,
                percentageUsed = 74f,
                categoryBreakdown = mapOf(
                    "Food" to 0.38f,
                    "Transport" to 0.22f,
                    "Rent" to 0.20f,
                    "Tuition" to 0.15f,
                    "Misc" to 0.05f
                ),
                monthlyTrend = listOf(
                    "Jun" to 1_200_000L,
                    "Jul" to 1_500_000L,
                    "Aug" to 1_100_000L,
                    "Sept" to 1_850_000L
                )
            )
        }
    }
}
