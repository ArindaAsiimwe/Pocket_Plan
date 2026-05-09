package com.example.pocketplan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseStatus { PENDING, COMPLETED, CANCELLED }

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val date: Long,
    val status: ExpenseStatus = ExpenseStatus.COMPLETED,
    val createdAt: Long = System.currentTimeMillis()
)
