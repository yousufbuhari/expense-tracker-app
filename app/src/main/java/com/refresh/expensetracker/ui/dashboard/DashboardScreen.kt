package com.refresh.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddExpense: () -> Unit = {},
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
            onAddExpense = onAddExpense,
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
    onAddExpense: () -> Unit,
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
                onClick = onAddExpense,
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
                    balance = String.format(Locale.getDefault(), "₹%.2f", balance),
                    trend = "+2.4% this week"
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
                        text = stringResource(R.string.recent_expenses),
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
fun TotalBalanceCard(balance: String, trend: String) {
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TOTAL BALANCE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Text(
                text = balance,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📈 $trend",
                    style = MaterialTheme.typography.labelMedium,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Medium
                )
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

@Composable
fun TransactionListItem(transaction: Transaction) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormatter.format(Date(transaction.date))
    val amountString = (if (transaction.isExpense) "-" else "+") + String.format(Locale.getDefault(), "₹%.2f", transaction.amount)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = transaction.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = amountString,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isExpense) ErrorRed else SuccessGreen
            )
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
            onAddExpense = {},
            onViewAll = {}
        )
    }
}
