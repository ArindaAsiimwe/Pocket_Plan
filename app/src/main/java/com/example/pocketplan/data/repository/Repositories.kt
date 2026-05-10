package com.example.pocketplan.data.repository

import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.Expense
import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    fun getCurrentUser(): Flow<User?>
    suspend fun restoreSession(): User?
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun logout()
}

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    fun getGoalsByStatus(status: String): Flow<List<Goal>>
    fun getTotalProtectedFunds(): Flow<Long?>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun getGoalById(id: String): Goal?
}

interface ExpenseRepository {
    fun getExpenses(userId: String): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
}
