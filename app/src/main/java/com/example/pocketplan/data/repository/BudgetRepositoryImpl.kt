package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.BudgetDao
import com.example.pocketplan.data.model.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {
    override fun getBudget(userId: String): Flow<Budget?> = budgetDao.getBudgetByUserId(userId)

    override suspend fun saveBudget(budget: Budget) {
        budgetDao.insertBudget(budget)
    }
}
