package com.buhari.moneymate.navigation

import com.buhari.moneymate.R

enum class AppDestinations(
    val label: Int,
    val icon: Int,
) {
    SPLASH(0, 0),
    DASHBOARD(R.string.dashboard, R.drawable.ic_dashboard),
    TRANSACTION(R.string.transactions, R.drawable.ic_receipt),
    STATS(R.string.stats, R.drawable.ic_stats),
    SETTINGS(R.string.settings, R.drawable.ic_settings),
}