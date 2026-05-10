package com.example.pocketplan.domain.usecase

import com.example.pocketplan.data.model.Expense
import com.example.pocketplan.data.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.insertExpense(expense)
    }
}
