package com.example.pocketplan.ui.goals

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.utils.ImageStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import java.util.UUID
import javax.inject.Inject

data class GoalsUiState(
    val isLoading: Boolean = false,
    val goals: List<Goal> = emptyList(),
    val portfolioHealthPercent: Int = 0,
    val isAddGoalSheetOpen: Boolean = false,
    val attachedImageUri: Uri? = null,
    val error: String? = null
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val imageStorageHelper: ImageStorageHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        // Load some dummy data for now
        _uiState.update {
            it.copy(
                goals = listOf(
                    Goal(UUID.randomUUID().toString(), "user1", "Rent", 500000.0, System.currentTimeMillis() + 86400000 * 30, "COMPLETED"),
                    Goal(UUID.randomUUID().toString(), "user1", "Tuition", 1500000.0, System.currentTimeMillis() + 86400000 * 60, "IN_PROGRESS")
                ),
                portfolioHealthPercent = 65
            )
        }
    }

    fun onAddGoalClick() {
        _uiState.update { it.copy(isAddGoalSheetOpen = true) }
    }

    fun onDismissSheet() {
        _uiState.update { it.copy(isAddGoalSheetOpen = false, attachedImageUri = null) }
    }

    fun onImagePicked(uri: Uri?) {
        viewModelScope.launch {
            val savedPath = uri?.let { imageStorageHelper.saveImageToInternalStorage(it) }
            // If there was an old temporary image, we might want to delete it, 
            // but here it's for a new goal being drafted.
            _uiState.update { it.copy(attachedImageUri = savedPath?.let { path -> Uri.parse(path) }) }
        }
    }

    fun updateGoalStatus(goalId: String, newStatus: String) {
        _uiState.update { state ->
            state.copy(
                goals = state.goals.map {
                    if (it.id == goalId) it.copy(status = newStatus) else it
                }
            )
        }
    }

    fun updateGoalImage(goalId: String, uri: Uri?) {
        viewModelScope.launch {
            val savedPath = uri?.let { imageStorageHelper.saveImageToInternalStorage(it) }
            
            _uiState.update { state ->
                val updatedGoals = state.goals.map { goal ->
                    if (goal.id == goalId) {
                        // Delete old image
                        goal.attachedImageUri?.let { oldPath ->
                            imageStorageHelper.deleteImageFromInternalStorage(oldPath)
                        }
                        goal.copy(attachedImageUri = savedPath)
                    } else goal
                }
                state.copy(goals = updatedGoals)
            }
        }
    }

    fun deleteGoalImage(goalId: String) {
        _uiState.update { state ->
            val updatedGoals = state.goals.map { goal ->
                if (goal.id == goalId) {
                    goal.attachedImageUri?.let { oldPath ->
                        imageStorageHelper.deleteImageFromInternalStorage(oldPath)
                    }
                    goal.copy(attachedImageUri = null)
                } else goal
            }
            state.copy(goals = updatedGoals)
        }
    }

    fun saveGoal(name: String, amount: Double, dueDate: Long) {
        val attachedPath = _uiState.value.attachedImageUri?.toString()
        val newGoal = Goal(
            id = UUID.randomUUID().toString(),
            userId = "user1",
            name = name,
            targetAmount = amount,
            dueDate = dueDate,
            status = "PENDING",
            attachedImageUri = attachedPath
        )
        _uiState.update {
            it.copy(
                goals = it.goals + newGoal,
                isAddGoalSheetOpen = false,
                attachedImageUri = null
            )
        }
    }
}
