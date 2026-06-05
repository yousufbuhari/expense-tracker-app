package com.refresh.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refresh.expensetracker.R
import com.refresh.expensetracker.data.Transaction
import com.refresh.expensetracker.ui.theme.ExpenseTrackerTheme
import com.refresh.expensetracker.ui.theme.SuccessGreen
import com.refresh.expensetracker.ui.theme.ErrorRed
import com.refresh.expensetracker.ui.theme.PrimaryPurple
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refresh.expensetracker.ui.viewmodel.TransactionViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import com.refresh.expensetracker.ui.components.TransactionListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit = {},
    onViewAll: () -> Unit = {},
    viewModel: TransactionViewModel = viewModel()
) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val totalIncome by viewModel.totalIncome.collectAsState(initial = 0.0)
    val totalExpense by viewModel.totalExpense.collectAsState(initial = 0.0)

    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            delay(3000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        DashboardContent(
            transactions = transactions,
            totalIncome = totalIncome ?: 0.0,
            totalExpense = totalExpense ?: 0.0,
            isLoading = isLoading,
            onAddTransaction = onAddTransaction,
            onViewAll = onViewAll
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    transactions: List<Transaction>,
    totalIncome: Double,
    totalExpense: Double,
    isLoading: Boolean,
    onAddTransaction: () -> Unit,
    onViewAll: () -> Unit
) {
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Finance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TotalBalanceCard(
                    balanceValue = balance,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummarySmallCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.monthly_spent),
                        value = String.format(Locale.getDefault(), "₹%.2f", totalExpense),
                        iconBackground = MaterialTheme.colorScheme.secondaryContainer,
                        iconRes = R.drawable.ic_receipt
                    )

                    val topCategoryGroup = transactions.filter { it.isExpense }
                        .groupBy { it.category }
                        .maxByOrNull { it.value.sumOf { t -> t.amount } }

                    val topCategoryName = topCategoryGroup?.key ?: "None"
                    val topCategoryIcon = topCategoryGroup?.value?.firstOrNull()?.icon ?: R.drawable.ic_other

                    SummarySmallCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.top_category),
                        value = topCategoryName,
                        iconBackground = MaterialTheme.colorScheme.secondaryContainer,
                        iconRes = topCategoryIcon
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_transactions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onViewAll) {
                        Text(stringResource(R.string.view_all), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            if (isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryPurple)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.loading_your_transactions),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                if (transactions.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_receipt),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_transactions_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(transactions.take(3)) { transaction ->
                        TransactionListItem(transaction)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TotalBalanceCard(
    balanceValue: Double,
    totalIncome: Double,
    totalExpense: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", balanceValue),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income Section
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "₹%.2f", totalIncome),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = stringResource(R.string.income),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Expense Section
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward ,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "₹%.2f", totalExpense),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = stringResource(R.string.expenses),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun SummarySmallCard(modifier: Modifier, label: String, value: String, iconBackground: Color, iconRes: Int) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// For preview
val recentTransactions = listOf(
    Transaction(1, "Starbucks", 5.50, System.currentTimeMillis(), "Food", true, R.drawable.ic_food),
    Transaction(2, "Uber", 12.00, System.currentTimeMillis() - 86400000, "Transport", true, R.drawable.ic_travel),
    Transaction(3, "Rent", 1200.00, System.currentTimeMillis() - 172800000, "Bills", true, R.drawable.ic_home),
    Transaction(4, "Whole Foods", 84.20, System.currentTimeMillis() - 259200000, "Groceries", true, R.drawable.ic_groceries)
)

@Preview
@Composable
private fun DashboardPreview() {
    ExpenseTrackerTheme {
        DashboardContent(
            transactions = recentTransactions,
            totalIncome = 5000.0,
            totalExpense = 750.0,
            isLoading = false,
            onAddTransaction = {},
            onViewAll = {}
        )
    }
}
