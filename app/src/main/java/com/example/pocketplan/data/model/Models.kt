package com.example.pocketplan.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String
)

data class Budget(
    val id: String,
    val userId: String,
    val totalAmount: Double,
    val months: List<String>,
    val categories: List<Category>
)

data class Category(
    val id: String,
    val name: String,
    val allocatedAmount: Double,
    val percentage: Double
)

data class Goal(
    val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val dueDate: Long,
    val status: String // e.g., "Active", "Completed"
)

data class Expense(
    val id: String,
    val userId: String,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val date: Long
)
