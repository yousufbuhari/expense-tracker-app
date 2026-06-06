package com.buhari.moneymate.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    fun formatAmount(amount: Double, currencyCode: String): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance(currencyCode)
            format.format(amount)
        } catch (e: Exception) {
            // Fallback if currency code is invalid or locale issues
            "${getSymbol(currencyCode)}${String.format(Locale.US, "%.2f", amount)}"
        }
    }

    fun getSymbol(currencyCode: String): String {
        return try {
            Currency.getInstance(currencyCode).symbol
        } catch (e: Exception) {
            when (currencyCode) {
                "INR" -> "₹"
                "USD" -> "$"
                "EUR" -> "€"
                "GBP" -> "£"
                "AED" -> "AED"
                "SAR" -> "SAR"
                "SGD" -> "S$"
                "AUD" -> "A$"
                "CAD" -> "C$"
                "JPY" -> "¥"
                "KWD" -> "KWD"
                "QAR" -> "QAR"
                else -> "$"
            }
        }
    }
}