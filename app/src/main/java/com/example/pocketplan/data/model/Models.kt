package com.example.pocketplan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val dueDate: Long,
    val status: String, // "PENDING", "IN_PROGRESS", "COMPLETED"
    val attachedImageUri: String? = null
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
