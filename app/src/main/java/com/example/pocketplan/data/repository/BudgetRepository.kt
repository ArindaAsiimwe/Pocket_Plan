package com.example.pocketplan.data.repository

import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.BudgetWithCategories
import com.example.pocketplan.data.model.Category
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<Budget>>
    fun getAllBudgetsWithCategories(): Flow<List<BudgetWithCategories>>
    fun getCurrentBudget(userId: String): Flow<Budget?>
    fun getBudgetById(budgetId: Long): Flow<Budget?>
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)

    fun getCategories(budgetId: Long): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}
