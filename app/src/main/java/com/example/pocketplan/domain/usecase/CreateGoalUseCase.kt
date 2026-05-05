package com.example.pocketplan.domain.usecase

import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.data.repository.GoalRepository
import javax.inject.Inject

class CreateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) {
        goalRepository.insertGoal(goal)
    }
}
