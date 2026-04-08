package com.example.pocketplan.data.local

import androidx.room.TypeConverter
import com.example.pocketplan.data.model.Category
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromCategoryList(value: List<Category>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCategoryList(value: String): List<Category> {
        val listType = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(value, listType)
    }
}
