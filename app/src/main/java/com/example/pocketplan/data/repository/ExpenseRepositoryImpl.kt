package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.ExpenseDao
import com.example.pocketplan.data.model.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    override fun getExpenses(userId: String): Flow<List<Expense>> = expenseDao.getExpensesByUserId(userId)

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }
}
