package com.buhari.moneymate.navigation

import com.buhari.moneymate.R

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    DASHBOARD("Dashboard", R.drawable.ic_dashboard),
    TRANSACTION("Transactions", R.drawable.ic_receipt),
    STATS("Stats", R.drawable.ic_stats),
    SETTINGS("Settings", R.drawable.ic_settings),
}