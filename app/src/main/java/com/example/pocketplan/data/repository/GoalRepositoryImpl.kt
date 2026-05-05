package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.dao.GoalDao
import com.example.pocketplan.data.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {
    override fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    override fun getGoalsByStatus(status: String): Flow<List<Goal>> = goalDao.getGoalsByStatus(status)

    override fun getTotalProtectedFunds(): Flow<Long?> = goalDao.getTotalProtectedFunds()

    override suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    override suspend fun getGoalById(id: String): Goal? = goalDao.getGoalById(id)
}
