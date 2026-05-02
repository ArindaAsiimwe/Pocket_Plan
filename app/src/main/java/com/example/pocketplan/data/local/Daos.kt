package com.example.pocketplan.data.local

import androidx.room.*
import com.example.pocketplan.data.model.Budget
import com.example.pocketplan.data.model.Expense
import com.example.pocketplan.data.model.Goal
import com.example.pocketplan.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId LIMIT 1")
    fun getBudgetByUserId(userId: String): Flow<Budget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE userId = :userId")
    fun getGoalsByUserId(userId: String): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId")
    fun getExpensesByUserId(userId: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    fun getExpensesInRange(userId: String, startDate: Long, endDate: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)
}
