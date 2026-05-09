package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.BudgetDao
import com.example.pocketplan.data.local.CategoryDao
import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.BudgetWithCategories
import com.example.pocketplan.data.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao
) : BudgetRepository {
    override fun getAllBudgets(): Flow<List<Budget>> = 
        budgetDao.getAllBudgets()

    override fun getAllBudgetsWithCategories(): Flow<List<BudgetWithCategories>> =
        budgetDao.getAllBudgetsWithCategories()

    override fun getCurrentBudget(userId: String): Flow<Budget?> = 
        budgetDao.getBudgetByUserId(userId)

    override fun getBudgetById(budgetId: Long): Flow<Budget?> = 
        budgetDao.getBudgetById(budgetId)

    override suspend fun insertBudget(budget: Budget): Long = 
        budgetDao.insertBudget(budget)

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    override suspend fun deleteBudget(budgetId: Long) {
        val budget = budgetDao.getBudgetById(budgetId).first()
        budget?.let { budgetDao.deleteBudget(it) }
    }

    override fun getCategories(budgetId: Long): Flow<List<Category>> =
        categoryDao.getCategoriesByBudgetId(budgetId)

    override fun getCategoriesByType(budgetId: Long, isBudget: Boolean): Flow<List<Category>> =
        categoryDao.getCategoriesByType(budgetId, isBudget)

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
}
