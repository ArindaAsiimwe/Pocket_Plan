package com.example.pocketplan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val id: String,
    val userId: String,
    val totalAmount: Double,
    val months: List<String>,
    val categories: List<Category>
)

enum class CategoryStatus { PENDING, IN_PROGRESS, COMPLETED }

data class Category(
    val id: String,
    val name: String,
    val allocatedAmount: Double,
    val percentage: Double,
    val icon: String = "default",
    val status: CategoryStatus = CategoryStatus.PENDING
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val dueDate: Long,
    val status: String // e.g., "Active", "Completed"
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val date: Long
)
