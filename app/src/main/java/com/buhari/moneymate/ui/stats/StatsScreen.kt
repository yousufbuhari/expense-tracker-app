package com.buhari.moneymate.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.R
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.ui.components.TransactionTypeToggle
import com.buhari.moneymate.ui.components.getCategoryNameRes
import com.buhari.moneymate.ui.viewmodel.TransactionViewModel
import com.buhari.moneymate.ui.theme.PrimaryPurple
import com.buhari.moneymate.ui.theme.SuccessGreen
import com.buhari.moneymate.ui.theme.ErrorRed
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextOverflow
import com.buhari.moneymate.ui.expenses.FilterState
import com.buhari.moneymate.ui.expenses.TransactionFilterScreen
import com.buhari.moneymate.ui.theme.LocalCurrency
import com.buhari.moneymate.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit = {},
    viewModel: TransactionViewModel = viewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(FilterState()) }
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val currencyCode = LocalCurrency.current
    
    val fmt = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    val filteredTransactions = remember(transactions, filterState) {
        transactions.filter { transaction ->
            val monthStr = fmt.format(Date(transaction.date))
            val monthMatch = filterState.selectedMonths.isEmpty() || filterState.selectedMonths.contains(monthStr)
            val categoryMatch = filterState.selectedCategories.isEmpty() || filterState.selectedCategories.contains(transaction.category)
            val currencyMatch = filterState.selectedCurrencies.isEmpty() || filterState.selectedCurrencies.contains(transaction.currencyCode)
            val paymentModeMatch = filterState.selectedPaymentModes.isEmpty() || filterState.selectedPaymentModes.contains(transaction.paymentMode)
            val typeMatch = transaction.isExpense == filterState.isExpense
            monthMatch && categoryMatch && currencyMatch && paymentModeMatch && typeMatch
        }
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
                    title = { Text(stringResource(R.string.stats), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = { showFilters = true }) {
                            val isFiltered = filterState.selectedMonths.isNotEmpty() || 
                                           filterState.selectedCategories.isNotEmpty() ||
                                           filterState.selectedCurrencies.isNotEmpty() ||
                                           filterState.selectedPaymentModes.isNotEmpty()
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.filter),
                                tint = if (isFiltered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TransactionTypeToggle(
                    isExpense = filterState.isExpense,
                    onTypeChange = { 
                        filterState = filterState.copy(isExpense = it, selectedCategories = emptySet()) 
                    }
                )

                if (filteredTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_receipt),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_data_to_display),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Summary Section
                    SummarySection(transactions = filteredTransactions, currencyCode = currencyCode, isExpense = filterState.isExpense)

                    // Pie Chart Card
                    ChartCard(title = stringResource(R.string.category_distribution)) {
                        val categoryData = remember(filteredTransactions) {
                            filteredTransactions.groupBy { it.category }
                                .mapValues { it.value.sumOf { t -> t.amount } }
                                .toList()
                                .sortedByDescending { it.second }
                                .toMap()
                        }
                        PieChart(data = categoryData)
                    }

                    // Bar Chart Card
                    ChartCard(title = stringResource(R.string.daily_trends)) {
                        val dailyData = remember(filteredTransactions) {
                            getDailyData(filteredTransactions)
                        }
                        BarChart(data = dailyData, color = if (filterState.isExpense) ErrorRed else SuccessGreen, currencyCode = currencyCode)
                    }

                    // Line Chart Card
                    ChartCard(title = stringResource(R.string.expense_income_flow)) {
                        val dailyData = remember(filteredTransactions) {
                            getDailyData(filteredTransactions)
                        }
                        LineChart(data = dailyData, color = if (filterState.isExpense) ErrorRed else SuccessGreen, currencyCode = currencyCode)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun SummarySection(transactions: List<Transaction>, currencyCode: String, isExpense: Boolean) {
    val totalAmount = transactions.sumOf { it.amount }
    val topCategory = transactions.groupBy { it.category }
        .mapValues { it.value.sumOf { t -> t.amount } }
        .maxByOrNull { it.value }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isExpense) ErrorRed.copy(alpha = 0.12f) else SuccessGreen.copy(alpha = 0.12f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                if (isExpense) ErrorRed.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    shape = CircleShape,
                    color = if (isExpense) ErrorRed.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isExpense) ErrorRed else SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isExpense) stringResource(R.string.total_expense) else stringResource(R.string.total_income),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isExpense) ErrorRed else SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = CurrencyUtils.formatAmount(totalAmount, currencyCode),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpense) ErrorRed else SuccessGreen
                )
            }
        }
        
        if (topCategory != null) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_stats),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.top_category),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(getCategoryNameRes(topCategory.key)),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun getDailyData(transactions: List<Transaction>): List<Pair<String, Double>> {
    val fmt = SimpleDateFormat("dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val dailyMap = transactions
        .groupBy { fmt.format(Date(it.date)) }
        .mapValues { it.value.sumOf { t -> t.amount } }
    
    // We try to show the full month range if it's the current month, 
    // or just the days that have data if it's a filtered multi-month view.
    // For simplicity, let's keep it daily for the "selected" context.
    return (1..daysInMonth).map { day ->
        val dayStr = day.toString().padStart(2, '0')
        dayStr to (dailyMap[dayStr] ?: 0.0)
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun PieChart(data: Map<String, Double>) {
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B),
        Color(0xFFEF4444), Color(0xFF8B5CF6), Color(0xFFEC4899),
        Color(0xFF06B6D4), Color(0xFF84CC16), Color(0xFFF43F5E)
    )
    
    val total = data.values.sum()
    var startAngle = 0f
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                data.values.forEachIndexed { index, value ->
                    val sweepAngle = (value / total).toFloat() * 360f
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true
                    )
                    startAngle += sweepAngle
                }
            }
            Surface(
                modifier = Modifier.size(75.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {}
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.entries.take(5).forEachIndexed { index, entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(colors[index % colors.size], RoundedCornerShape(3.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    val label = entry.key
                    val displayLabel = if (label in listOf("Housing", "Food", "Beverages", "Groceries", "Shopping", "Fuel", "Entertainment", "Travel", "Bills", "Finance", "Health", "Sports", "Family", "Pets", "Lending", "Salary", "Freelance", "Business", "Investment", "Rental", "Bonus", "Gift", "Refund", "Other")) {
                        stringResource(getCategoryNameRes(label))
                    } else {
                        label
                    }
                    Text(
                        text = displayLabel,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${(entry.value / total * 100).toInt()}%", 
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.End
                    )
                }
            }
            if (data.size > 5) {
                Text(
                    text = "+ ${data.size - 5} more", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 18.dp)
                )
            }
        }
    }
}

@Composable
fun BarChart(data: List<Pair<String, Double>>, color: Color = PrimaryPurple, currencyCode: String) {
    val maxVal = (data.maxByOrNull { it.second }?.second ?: 0.0).coerceAtLeast(1.0)
    
    Column {
        Row(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            // Y-Axis
            Column(
                modifier = Modifier.fillMaxHeight().width(45.dp).padding(bottom = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(text = formatCompact(maxVal, currencyCode), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                Text(text = formatCompact(maxVal / 2, currencyCode), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                Text(text = "0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Grid lines
                    val lineCount = 3
                    for (i in 0 until lineCount) {
                        val y = size.height - (i * size.height / (lineCount - 1))
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val barWidth = (size.width / (data.size * 1.3f)).coerceAtLeast(2.dp.toPx())
                    val space = (size.width - (barWidth * data.size)) / (data.size + 1)
                    
                    data.forEachIndexed { index, pair ->
                        val barHeight = (pair.second / maxVal).toFloat() * size.height
                        val x = space + index * (barWidth + space)
                        val y = size.height - barHeight
                        
                        drawRect(
                            color = color.copy(alpha = if (pair.second > 0) 1f else 0.05f),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight.coerceAtLeast(1f)),
                            style = androidx.compose.ui.graphics.drawscope.Fill
                        )
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 53.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEachIndexed { index, pair ->
                if (index % 5 == 0 || index == data.size - 1) {
                    Text(text = pair.first, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun LineChart(data: List<Pair<String, Double>>, color: Color = PrimaryPurple, currencyCode: String) {
    val maxVal = (data.maxByOrNull { it.second }?.second ?: 0.0).coerceAtLeast(1.0)
    
    Column {
        Row(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            // Y-Axis
            Column(
                modifier = Modifier.fillMaxHeight().width(45.dp).padding(bottom = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(text = formatCompact(maxVal, currencyCode), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                Text(text = formatCompact(maxVal / 2, currencyCode), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                Text(text = "0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Grid lines
                    val lineCount = 3
                    for (i in 0 until lineCount) {
                        val y = size.height - (i * size.height / (lineCount - 1))
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (data.size < 2) return@Canvas
                    
                    val distance = size.width / (data.size - 1)
                    val path = Path()
                    
                    data.forEachIndexed { index, pair ->
                        val x = index * distance
                        val y = size.height - (pair.second / maxVal).toFloat() * size.height
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }
                    
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Draw gradient area under the line
                    val fillPath = Path().apply {
                        val lastX = (data.size - 1) * distance
                        addPath(path)
                        lineTo(lastX, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(color.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    
                    data.forEachIndexed { index, pair ->
                        if (pair.second > 0) {
                            val x = index * distance
                            val y = size.height - (pair.second / maxVal).toFloat() * size.height
                            drawCircle(
                                color = color,
                                radius = 3.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 53.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEachIndexed { index, pair ->
                if (index % 5 == 0 || index == data.size - 1) {
                    Text(text = pair.first, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

private fun formatCompact(value: Double, currencyCode: String): String {
    val symbol = com.buhari.moneymate.utils.CurrencyUtils.getSymbol(currencyCode)
    return when {
        value >= 1000000 -> symbol + String.format("%.1fM", value / 1000000)
        value >= 1000 -> symbol + String.format("%.1fK", value / 1000)
        else -> symbol + value.toInt().toString()
    }
}
