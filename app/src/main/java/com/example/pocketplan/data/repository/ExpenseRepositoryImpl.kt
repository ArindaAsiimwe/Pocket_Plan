package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.BudgetDao
import com.example.pocketplan.data.local.dao.ExpenseDao
import com.example.pocketplan.data.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
) : ExpenseRepository {

    override fun getRecentExpenses(userId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByUserId(userId)
    }

    override fun getTotalSpent(userId: String): Flow<Long?> {
        return expenseDao.getTotalSpent(userId)
    }

    override fun getExpensesByCategory(userId: String, categoryId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(userId, categoryId)
    }

    override fun getMonthlyTrend(userId: String): Flow<Map<String, Double>> {
        return expenseDao.getExpensesByUserId(userId).map { expenses ->
            val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            expenses.groupBy { 
                sdf.format(Date(it.date)) 
            }.mapValues { entry -> 
                entry.value.sumOf { it.amount } 
            }
        }
    }

    override suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
