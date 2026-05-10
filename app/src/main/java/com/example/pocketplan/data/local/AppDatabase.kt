package com.example.pocketplan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pocketplan.data.model.*
import com.example.pocketplan.data.local.dao.GoalDao
import com.example.pocketplan.data.local.dao.ExpenseDao

@Database(
    entities = [
        Budget::class,
        Category::class,
        Goal::class,
        Expense::class
    ],
    version = 6
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun expenseDao(): ExpenseDao
}
