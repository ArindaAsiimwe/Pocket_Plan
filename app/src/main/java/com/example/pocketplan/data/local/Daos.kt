package com.example.pocketplan.data.local

import androidx.room.*
import com.example.pocketplan.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

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

    @Query("SELECT * FROM categories WHERE budgetId = :budgetId AND isBudgetCategory = :isBudget")
    fun getCategoriesByType(budgetId: Long, isBudget: Boolean): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}
