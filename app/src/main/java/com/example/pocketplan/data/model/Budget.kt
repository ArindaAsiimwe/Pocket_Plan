package com.example.pocketplan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val semesterName: String,
    val totalFunds: Long,
    val selectedMonths: List<String>,
    val createdDate: Long
)

data class BudgetWithCategories(
    @androidx.room.Embedded val budget: Budget,
    @Relation(
        parentColumn = "id",
        entityColumn = "budgetId"
    )
    val categories: List<Category>
)
