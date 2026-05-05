package com.example.pocketplan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GoalStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val currentProgress: Double,
    val status: GoalStatus,
    val dueDate: Long,
    val imagePath: String?,
    val createdAt: Long = System.currentTimeMillis()
)
