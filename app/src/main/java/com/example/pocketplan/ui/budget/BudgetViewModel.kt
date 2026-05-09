package com.example.pocketplan.ui.budget

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.Category
import com.example.pocketplan.data.model.CategoryStatus
import com.example.pocketplan.data.repository.BudgetRepository
import com.example.pocketplan.utils.ImageStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetUiState(
    val id: Long = 0L,
    val semesterName: String = "",
    val totalFunds: Long = 0L,
    val selectedMonths: List<String> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isEditing: Boolean = false,
    val attachedImageUri: Uri? = null,
    val expandedAttachmentIds: Set<String> = emptySet(),
    val createdDate: Long = 0L
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository,
    private val imageStorageHelper: ImageStorageHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private var budgetJob: Job? = null
    private var categoriesJob: Job? = null

    fun loadBudget(budgetId: Long) {
        if (budgetId <= 0) return
        
        budgetJob?.cancel()
        categoriesJob?.cancel()

        budgetJob = viewModelScope.launch {
            repository.getBudgetById(budgetId).collectLatest { budget ->
                budget?.let { b ->
                    _uiState.update { 
                        it.copy(
                            id = b.id,
                            semesterName = b.semesterName,
                            totalFunds = b.totalFunds,
                            selectedMonths = b.selectedMonths,
                            createdDate = b.createdDate
                        )
                    }
                }
            }
        }

        categoriesJob = viewModelScope.launch {
            repository.getCategoriesByType(budgetId, true).collectLatest { categories ->
                _uiState.update { it.copy(categories = categories.sortedByDescending { it.id }) }
            }
        }
    }

    fun updateTotalFunds(amount: Long) {
        _uiState.update { it.copy(totalFunds = amount) }
        recalculateCategoryAmounts()
    }

    fun toggleMonth(month: String) {
        _uiState.update { state ->
            val newMonths = if (state.selectedMonths.contains(month)) {
                state.selectedMonths.filter { it != month }
            } else {
                state.selectedMonths + month
            }
            state.copy(selectedMonths = newMonths)
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val newCategory = Category(
                budgetId = currentState.id,
                name = name,
                allocatedAmount = 0L,
                percentage = 0.0
            )
            repository.insertCategory(newCategory)
        }
    }

    fun removeCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun updateCategoryPercentage(categoryId: Long, percentage: Double) {
        val category = _uiState.value.categories.find { it.id == categoryId } ?: return
        viewModelScope.launch {
            val updatedCategory = category.copy(
                percentage = percentage,
                allocatedAmount = (_uiState.value.totalFunds * percentage / 100.0).toLong()
            )
            repository.updateCategory(updatedCategory)
        }
    }

    fun updateCategoryAmount(categoryId: Long, amount: Double) {
        val category = _uiState.value.categories.find { it.id == categoryId } ?: return
        viewModelScope.launch {
            val totalFunds = _uiState.value.totalFunds
            val percentage = if (totalFunds > 0) (amount / totalFunds) * 100.0 else 0.0
            val updatedCategory = category.copy(
                allocatedAmount = amount.toLong(),
                percentage = percentage
            )
            repository.updateCategory(updatedCategory)
        }
    }

    private fun recalculateCategoryAmounts() {
        val currentState = _uiState.value
        currentState.categories.forEach { category ->
            viewModelScope.launch {
                val newAmount = (currentState.totalFunds * category.percentage / 100.0).toLong()
                if (newAmount != category.allocatedAmount) {
                    repository.updateCategory(category.copy(allocatedAmount = newAmount))
                }
            }
        }
    }

    fun toggleAttachmentSection(categoryId: Long) {
        _uiState.update { state ->
            val newExpanded = if (state.expandedAttachmentIds.contains(categoryId.toString())) {
                state.expandedAttachmentIds - categoryId.toString()
            } else {
                state.expandedAttachmentIds + categoryId.toString()
            }
            state.copy(expandedAttachmentIds = newExpanded)
        }
    }

    fun updateCategoryPhoto(categoryId: Long, uri: Uri?) {
        val category = _uiState.value.categories.find { it.id == categoryId } ?: return
        viewModelScope.launch {
            val savedPath = uri?.let { imageStorageHelper.saveImageToInternalStorage(it) }
            
            // Delete old image if it exists
            category.attachedImageUri?.let { oldPath ->
                imageStorageHelper.deleteImageFromInternalStorage(oldPath)
            }
            
            repository.updateCategory(category.copy(attachedImageUri = savedPath))
        }
    }

    fun updateCategoryStatus(categoryId: Long, status: CategoryStatus) {
        val category = _uiState.value.categories.find { it.id == categoryId } ?: return
        viewModelScope.launch {
            repository.updateCategory(category.copy(status = status))
        }
    }

    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun saveBudget() {
        viewModelScope.launch {
            val state = _uiState.value
            val budget = Budget(
                id = state.id,
                semesterName = state.semesterName,
                totalFunds = state.totalFunds,
                selectedMonths = state.selectedMonths,
                createdDate = if (state.createdDate == 0L) System.currentTimeMillis() else state.createdDate
            )
            if (state.id == 0L) {
                val newId = repository.insertBudget(budget)
                _uiState.update { it.copy(id = newId, isEditing = false) }
                loadBudget(newId)
            } else {
                repository.updateBudget(budget)
                _uiState.update { it.copy(isEditing = false) }
            }
        }
    }
}
