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
    suspend fun restoreSession(): User?
    suspend fun logout()
    suspend fun updateProfilePicture(userId: String, path: String): Result<Unit>
    suspend fun updateName(userId: String, newName: String): Result<Unit>
    suspend fun updatePassword(userId: String, newPasswordHash: String): Result<Unit>
}

interface GoalRepository {
    fun getAllGoals(userId: String): Flow<List<Goal>>
    fun getGoalsByStatus(userId: String, status: String): Flow<List<Goal>>
    fun getTotalProtectedFunds(userId: String): Flow<Long?>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun getGoalById(id: String): Goal?
}
