package com.example.pocketplan.data.local

import androidx.room.*
import com.example.pocketplan.data.model.*
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
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Transaction
    @Query("SELECT * FROM budgets")
    fun getAllBudgetsWithCategories(): Flow<List<BudgetWithCategories>>

    @Query("SELECT * FROM budgets WHERE userId = :userId LIMIT 1")
    fun getBudgetByUserId(userId: String): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    fun getBudgetById(budgetId: Long): Flow<Budget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE budgetId = :budgetId")
    fun getCategoriesByBudgetId(budgetId: Long): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE userId = :userId")
    fun getGoalsByUserId(userId: String): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId")
    fun getExpensesByUserId(userId: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    fun getExpensesInRange(userId: String, startDate: Long, endDate: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}
