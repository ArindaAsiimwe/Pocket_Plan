package com.example.pocketplan.domain.usecase

import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetSummaryUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(userId: String): Flow<Budget?> {
        return budgetRepository.getCurrentBudget(userId)
    }
}
