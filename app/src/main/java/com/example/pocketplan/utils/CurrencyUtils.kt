package com.example.pocketplan.utils

import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    /**
     * Formats an amount to Ugandan Shillings (UGX)
     */
    fun formatToUGX(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "UG"))
        // Often UGX doesn't use decimals in common practice, 
        // but standard format might include them.
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}
