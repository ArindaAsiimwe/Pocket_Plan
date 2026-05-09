package com.example.pocketplan.data.local.dao

import androidx.room.*
import com.example.pocketplan.data.model.Expense
import com.example.pocketplan.data.model.ExpenseStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT CAST(SUM(amount) AS INTEGER) FROM expenses WHERE userId = :userId")
    fun getTotalSpent(userId: String): Flow<Long?>

    @Query("SELECT * FROM expenses WHERE status = :status ORDER BY createdAt DESC")
    fun getExpensesByStatus(status: ExpenseStatus): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY createdAt DESC")
    fun getExpensesByUserId(userId: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND categoryId = :categoryId ORDER BY createdAt DESC")
    fun getExpensesByCategory(userId: String, categoryId: String): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: String): Expense?
}
