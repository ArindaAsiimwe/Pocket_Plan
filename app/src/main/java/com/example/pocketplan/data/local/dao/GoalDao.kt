package com.example.pocketplan.data.local.dao

import androidx.room.*
import com.example.pocketplan.data.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllGoals(userId: String): Flow<List<Goal>>

    @Query("SELECT CAST(SUM(targetAmount) AS INTEGER) FROM goals WHERE userId = :userId")
    fun getTotalProtectedFunds(userId: String): Flow<Long?>

    @Query("SELECT * FROM goals WHERE userId = :userId AND status = :status")
    fun getGoalsByStatus(userId: String, status: String): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: String): Goal?
}
