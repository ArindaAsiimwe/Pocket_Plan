package com.example.pocketplan.data.local.dao

import androidx.room.*
import com.example.pocketplan.data.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT CAST(SUM(targetAmount) AS INTEGER) FROM goals")
    fun getTotalProtectedFunds(): Flow<Long?>

    @Query("SELECT * FROM goals WHERE status = :status")
    fun getGoalsByStatus(status: String): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: String): Goal?
}
