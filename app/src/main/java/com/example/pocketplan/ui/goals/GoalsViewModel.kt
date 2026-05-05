package com.example.pocketplan.ui.goals

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.data.model.GoalStatus
import com.example.pocketplan.data.repository.GoalRepository
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
    val expandedGoalIds: Set<String> = emptySet(),
    val portfolioHealthPercent: Int = 0,
    val isAddGoalSheetOpen: Boolean = false,
    val attachedImageUri: Uri? = null,
    val error: String? = null
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val imageStorageHelper: ImageStorageHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalsUiState(isLoading = true))
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            goalRepository.getAllGoals().collect { goals ->
                val totalTarget = goals.sumOf { it.targetAmount }
                val totalProgress = goals.sumOf { it.currentProgress }
                val health = if (totalTarget > 0) {
                    ((totalProgress / totalTarget) * 100).toInt()
                } else {
                    0
                }
                _uiState.update { it.copy(
                    goals = goals,
                    portfolioHealthPercent = health,
                    isLoading = false
                ) }
            }
        }
    }

    fun onAddGoalClick() {
        _uiState.update { it.copy(isAddGoalSheetOpen = true) }
    }

    fun toggleGoalExpansion(goalId: String) {
        _uiState.update { state ->
            val newExpandedIds = if (state.expandedGoalIds.contains(goalId)) {
                state.expandedGoalIds - goalId
            } else {
                state.expandedGoalIds + goalId
            }
            state.copy(expandedGoalIds = newExpandedIds)
        }
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
        val statusEnum = try { GoalStatus.valueOf(newStatus) } catch (e: Exception) { GoalStatus.PENDING }
        viewModelScope.launch {
            val goal = goalRepository.getGoalById(goalId)
            goal?.let {
                goalRepository.updateGoal(it.copy(status = statusEnum))
            }
        }
    }

    fun updateGoalImage(goalId: String, uri: Uri?) {
        viewModelScope.launch {
            val savedPath = uri?.let { imageStorageHelper.saveImageToInternalStorage(it) }
            
            val goal = goalRepository.getGoalById(goalId)
            goal?.let {
                // Delete old image if exists
                it.imagePath?.let { oldPath ->
                    imageStorageHelper.deleteImageFromInternalStorage(oldPath)
                }
                goalRepository.updateGoal(it.copy(imagePath = savedPath))
            }
        }
    }

    fun deleteGoalImage(goalId: String) {
        viewModelScope.launch {
            val goal = goalRepository.getGoalById(goalId)
            goal?.let {
                it.imagePath?.let { oldPath ->
                    imageStorageHelper.deleteImageFromInternalStorage(oldPath)
                }
                goalRepository.updateGoal(it.copy(imagePath = null))
            }
        }
    }

    fun saveGoal(name: String, amount: Double, dueDate: Long) {
        val attachedPath = _uiState.value.attachedImageUri?.path // Get the actual file path
        val newGoal = Goal(
            id = UUID.randomUUID().toString(),
            userId = "user1", // Should ideally come from an AuthRepository
            name = name,
            targetAmount = amount,
            currentProgress = 0.0,
            status = GoalStatus.PENDING,
            dueDate = dueDate,
            imagePath = attachedPath,
            createdAt = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            try {
                goalRepository.insertGoal(newGoal)
                _uiState.update { 
                    it.copy(
                        isAddGoalSheetOpen = false,
                        attachedImageUri = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goal.imagePath?.let {
                imageStorageHelper.deleteImageFromInternalStorage(it)
            }
            goalRepository.deleteGoal(goal)
        }
    }
}
