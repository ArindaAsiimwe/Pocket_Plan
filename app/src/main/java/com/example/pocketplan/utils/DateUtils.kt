package com.example.pocketplan.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatLongToDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }
}
