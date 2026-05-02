package com.example.pocketplan.data.repository

import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.Expense
import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, passwordHash: String): Result<User>
    suspend fun register(name: String, email: String, passwordHash: String): Result<User>
    fun getCurrentUser(): Flow<User?>
}

interface GoalRepository {
    fun getGoals(userId: String): Flow<List<Goal>>
    suspend fun addGoal(goal: Goal)
}

interface ExpenseRepository {
    fun getExpenses(userId: String): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
}
