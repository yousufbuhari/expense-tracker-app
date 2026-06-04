package com.refresh.expensetracker.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.refresh.expensetracker.navigation.components.AnimatedNavIcon
import com.refresh.expensetracker.navigation.components.AnimatedNavLabel
import com.refresh.expensetracker.ui.addtransaction.AddTransactionScreen
import com.refresh.expensetracker.ui.dashboard.DashboardScreen
import com.refresh.expensetracker.ui.expenses.TransactionScreen
import com.refresh.expensetracker.ui.theme.NavBgDark
import com.refresh.expensetracker.ui.theme.NavBgLight
import com.refresh.expensetracker.ui.theme.NavSelectedBgDark
import com.refresh.expensetracker.ui.theme.NavSelectedBgLight
import com.refresh.expensetracker.ui.theme.NavSelectedContentDark
import com.refresh.expensetracker.ui.theme.NavSelectedContentLight
import com.refresh.expensetracker.ui.theme.NavUnselectedContentDark
import com.refresh.expensetracker.ui.theme.NavUnselectedContentLight

@PreviewScreenSizes
@Composable
fun ExpenseTrackerApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    var showAddTransaction by rememberSaveable { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()

    BackHandler(enabled = showAddTransaction || currentDestination != AppDestinations.DASHBOARD) {
        if (showAddTransaction) {
            showAddTransaction = false
        } else {
            currentDestination = AppDestinations.DASHBOARD
        }
    }

    Crossfade(
        targetState = showAddTransaction,
        animationSpec = tween(150),
        label = "ScreenTransition"
    ) { targetShowAddTransaction ->
        if (targetShowAddTransaction) {
            AddTransactionScreen(onClose = { showAddTransaction = false })
        } else {
            val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
                navigationBarItemColors = NavigationBarItemDefaults.colors(
                    indicatorColor = if (isDarkTheme) NavSelectedBgDark else NavSelectedBgLight,
                    selectedIconColor = if (isDarkTheme) NavSelectedContentDark else NavSelectedContentLight,
                    selectedTextColor = if (isDarkTheme) NavSelectedContentDark else NavSelectedContentLight,
                    unselectedIconColor = if (isDarkTheme) NavUnselectedContentDark else NavUnselectedContentLight,
                    unselectedTextColor = if (isDarkTheme) NavUnselectedContentDark else NavUnselectedContentLight,
                ),
                navigationRailItemColors = NavigationRailItemDefaults.colors(
                    indicatorColor = if (isDarkTheme) NavSelectedBgDark else NavSelectedBgLight,
                    selectedIconColor = if (isDarkTheme) NavSelectedContentDark else NavSelectedContentLight,
                    selectedTextColor = if (isDarkTheme) NavSelectedContentDark else NavSelectedContentLight,
                    unselectedIconColor = if (isDarkTheme) NavUnselectedContentDark else NavUnselectedContentLight,
                    unselectedTextColor = if (isDarkTheme) NavUnselectedContentDark else NavUnselectedContentLight,
                )
            )

            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.forEach { destination ->
                        val isSelected = destination == currentDestination
                        item(
                            icon = {
                                AnimatedNavIcon(
                                    destination = destination,
                                    isSelected = isSelected
                                )
                            },
                            label = {
                                AnimatedNavLabel(
                                    destination = destination,
                                    isSelected = isSelected
                                )
                            },
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            colors = myNavigationSuiteItemColors
                        )
                    }
                },
                navigationSuiteColors = NavigationSuiteDefaults.colors(
                    navigationBarContainerColor = if (isDarkTheme) NavBgDark else NavBgLight,
                    navigationRailContainerColor = if (isDarkTheme) NavBgDark else NavBgLight,
                )
            ) {
                when (currentDestination) {
                    AppDestinations.DASHBOARD -> DashboardScreen(
                        onAddTransaction = { showAddTransaction = true },
                        onViewAll = { currentDestination = AppDestinations.TRANSACTION }
                    )
                    AppDestinations.TRANSACTION -> TransactionScreen(
                        onBack = { currentDestination = AppDestinations.DASHBOARD }
                    )
                    AppDestinations.STATS -> Text("Stats Screen")
                    AppDestinations.SETTINGS -> Text("Settings Screen")
                }
            }
        }
    }
}
