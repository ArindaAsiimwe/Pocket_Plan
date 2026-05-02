package com.example.pocketplan.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class CategoryStatus { PENDING, IN_PROGRESS, COMPLETED }

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = Budget::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("budgetId")]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val budgetId: Long,
    val name: String,
    val allocatedAmount: Long,
    val percentage: Double,
    val icon: String = "default",
    val status: CategoryStatus = CategoryStatus.PENDING,
    val attachedImageUri: String? = null
)
