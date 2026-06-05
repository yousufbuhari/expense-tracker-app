package com.refresh.expensetracker.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refresh.expensetracker.R
import com.refresh.expensetracker.ui.components.TransactionListItem
import com.refresh.expensetracker.ui.components.TransactionTypeToggle
import com.refresh.expensetracker.ui.viewmodel.TransactionViewModel
import com.refresh.expensetracker.ui.theme.PrimaryPurple
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBack: () -> Unit = {},
    viewModel: TransactionViewModel = viewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(FilterState()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    
    val fmt = remember { SimpleDateFormat("MMM yyyy", Locale.getDefault()) }

    val filteredTransactions = transactions.filter { transaction ->
        val monthStr = fmt.format(Date(transaction.date))
        val monthMatch = filterState.selectedMonths.isEmpty() || filterState.selectedMonths.contains(monthStr)
        val categoryMatch = filterState.selectedCategories.isEmpty() || filterState.selectedCategories.contains(transaction.category)
        val typeMatch = transaction.isExpense == filterState.isExpense
        monthMatch && categoryMatch && typeMatch
    }

    if (showFilters) {
        TransactionFilterScreen(
            initialState = filterState,
            onBack = { showFilters = false },
            onApply = { 
                filterState = it
                showFilters = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.all_transactions), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFilters = true }) {
                            val isFiltered = filterState.selectedMonths.isNotEmpty() || filterState.selectedCategories.isNotEmpty()
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (isFiltered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TransactionTypeToggle(
                        isExpense = filterState.isExpense,
                        onTypeChange = { 
                            filterState = filterState.copy(isExpense = it, selectedCategories = emptySet()) 
                        }
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryPurple)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.loading_your_transactions),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else if (filteredTransactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_receipt),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (filterState.selectedMonths.isNotEmpty() || filterState.selectedCategories.isNotEmpty())
                                    stringResource(R.string.no_matches_for_your_filters) else stringResource(R.string.no_transactions_found),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            if (filterState.selectedMonths.isNotEmpty() || filterState.selectedCategories.isNotEmpty()) {
                                TextButton(onClick = { filterState = FilterState() }) {
                                    Text("Clear Filters")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredTransactions) { transaction ->
                            TransactionListItem(transaction)
                        }
                    }
                }
            }
        }
    }
}
