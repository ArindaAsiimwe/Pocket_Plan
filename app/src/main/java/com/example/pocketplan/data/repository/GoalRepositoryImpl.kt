package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.GoalDao
import com.example.pocketplan.data.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {
    override fun getGoals(userId: String): Flow<List<Goal>> = goalDao.getGoalsByUserId(userId)

    override suspend fun addGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }
}
