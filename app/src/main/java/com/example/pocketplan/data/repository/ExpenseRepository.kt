package com.example.pocketplan.data.repository

import com.example.pocketplan.data.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getRecentExpenses(userId: String): Flow<List<Expense>>
    fun getTotalSpent(userId: String): Flow<Long?>
    fun getExpensesByCategory(userId: String, categoryId: String): Flow<List<Expense>>
    fun getMonthlyTrend(userId: String): Flow<Map<String, Double>>
    suspend fun insertExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}
