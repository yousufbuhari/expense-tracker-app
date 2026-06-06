package com.buhari.moneymate.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.buhari.moneymate.R
import com.buhari.moneymate.navigation.components.AnimatedNavIcon
import com.buhari.moneymate.navigation.components.AnimatedNavLabel
import com.buhari.moneymate.ui.addtransaction.AddTransactionScreen
import com.buhari.moneymate.ui.dashboard.DashboardScreen
import com.buhari.moneymate.ui.expenses.TransactionScreen
import com.buhari.moneymate.ui.settings.SettingsScreen

@PreviewScreenSizes
@Composable
fun MoneyMateApp(language: String = "en") {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    var showAddTransaction by rememberSaveable { mutableStateOf(false) }
    var editingTransactionId by rememberSaveable { mutableStateOf<Int?>(null) }

    BackHandler(enabled = showAddTransaction || currentDestination != AppDestinations.DASHBOARD) {
        if (showAddTransaction) {
            showAddTransaction = false
            editingTransactionId = null
        } else {
            currentDestination = AppDestinations.DASHBOARD
        }
    }

    key(language) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = showAddTransaction,
                transitionSpec = {
                    if (targetState) {
                        (slideInVertically(
                            animationSpec = spring(
                                dampingRatio = 0.85f,
                                stiffness = 300f
                            ),
                            initialOffsetY = { it }
                        ) + fadeIn(animationSpec = tween(400))).togetherWith(
                            fadeOut(animationSpec = tween(300))
                        )
                    } else {
                        (fadeIn(animationSpec = tween(400))).togetherWith(
                            slideOutVertically(
                                animationSpec = spring(
                                    dampingRatio = 0.85f,
                                    stiffness = 300f
                                ),
                                targetOffsetY = { it }
                            ) + fadeOut(animationSpec = tween(400))
                        )
                    }
                },
                label = "ScreenTransition"
            ) { targetShowAddTransaction ->
                if (targetShowAddTransaction) {
                    AddTransactionScreen(
                        transactionId = editingTransactionId,
                        onClose = {
                            showAddTransaction = false
                            editingTransactionId = null
                        }
                    )
                } else {
                    val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
                        navigationBarItemColors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        navigationRailItemColors = NavigationRailItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            navigationBarContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            navigationBarContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            navigationRailContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        when (currentDestination) {
                            AppDestinations.DASHBOARD -> DashboardScreen(
                                onAddTransaction = {
                                    editingTransactionId = null
                                    showAddTransaction = true
                                },
                                onEditTransaction = { id ->
                                    editingTransactionId = id
                                    showAddTransaction = true
                                },
                                onViewAll = { currentDestination = AppDestinations.TRANSACTION }
                            )
                            AppDestinations.TRANSACTION -> TransactionScreen(
                                onBack = { currentDestination = AppDestinations.DASHBOARD },
                                onEditTransaction = { id ->
                                    editingTransactionId = id
                                    showAddTransaction = true
                                }
                            )
                            AppDestinations.STATS -> Text(stringResource(R.string.stats))
                            AppDestinations.SETTINGS -> SettingsScreen(
                                onBack = { currentDestination = AppDestinations.DASHBOARD }
                            )
                        }
                    }
                }
            }
        }
    }
}
