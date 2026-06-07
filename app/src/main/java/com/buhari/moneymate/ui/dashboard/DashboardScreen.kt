package com.buhari.moneymate.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buhari.moneymate.R
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.theme.SuccessGreen
import com.buhari.moneymate.ui.theme.ErrorRed
import com.buhari.moneymate.ui.theme.PrimaryPurple
import com.buhari.moneymate.ui.theme.LocalCurrency
import com.buhari.moneymate.utils.CurrencyUtils
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.ui.viewmodel.TransactionViewModel
import com.buhari.moneymate.ui.viewmodel.SettingsViewModel
import com.buhari.moneymate.data.entity.UserProfile
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import coil3.compose.AsyncImage
import com.buhari.moneymate.ui.components.TransactionDetailSheetContent
import com.buhari.moneymate.ui.components.TransactionListItem
import com.buhari.moneymate.ui.components.getCategoryNameRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit = {},
    onEditTransaction: (Int) -> Unit = {},
    onViewAll: () -> Unit = {},
    viewModel: TransactionViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val userProfileState by settingsViewModel.userProfile.collectAsState()

    val userProfile = userProfileState ?: return // Don't render until profile is loaded

    val totalIncome by viewModel.totalIncome.collectAsState(initial = 0.0)
    val totalExpense by viewModel.totalExpense.collectAsState(initial = 0.0)

    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTransactionForDetail by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(500)
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
            userProfile = userProfile,
            totalIncome = totalIncome ?: 0.0,
            totalExpense = totalExpense ?: 0.0,
            isLoading = isLoading,
            onAddTransaction = onAddTransaction,
            onEditTransaction = onEditTransaction,
            onDeleteTransaction = { viewModel.deleteTransaction(it) },
            onLongClickTransaction = { selectedTransactionForDetail = it },
            onViewAll = onViewAll
        )
    }

    if (selectedTransactionForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedTransactionForDetail = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TransactionDetailSheetContent(transaction = selectedTransactionForDetail!!)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    transactions: List<Transaction>,
    userProfile: UserProfile,
    totalIncome: Double,
    totalExpense: Double,
    isLoading: Boolean,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Int) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onLongClickTransaction: (Transaction) -> Unit,
    onViewAll: () -> Unit
) {
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(42.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            if (userProfile.profileImage.isNullOrEmpty()) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_profile),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = userProfile.profileImage,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.hi_user, userProfile.name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.welcome_back),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(if (isPressed) 0.85f else 1f, label = "FABScale")

            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                shape = CircleShape,
                interactionSource = interactionSource,
                modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_expense_desc))
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
                    val currencyCode = LocalCurrency.current
                    SummarySmallCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.monthly_spent),
                        value = CurrencyUtils.formatAmount(totalExpense, currencyCode),
                        iconBackground = MaterialTheme.colorScheme.secondaryContainer,
                        iconRes = R.drawable.ic_receipt
                    )

                    val topCategoryGroup = transactions.filter { it.isExpense }
                        .groupBy { it.category }
                        .maxByOrNull { it.value.sumOf { t -> t.amount } }

                    val topCategoryName = if (topCategoryGroup != null) {
                        stringResource(getCategoryNameRes(topCategoryGroup.key))
                    } else {
                        stringResource(R.string.none)
                    }
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
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = stringResource(R.string.loading_your_transactions),
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.primary
//                        )
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
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            transactions.take(3).forEach { transaction ->
                                TransactionListItem(
                                    transaction = transaction,
                                    onEdit = { onEditTransaction(it.id) },
                                    onDelete = { onDeleteTransaction(it) },
                                    onLongClick = { onLongClickTransaction(it) }
                                )
                            }
                        }
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
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            PrimaryPurple,
            Color(0xFF8E44AD)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurple)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            // Background decorative circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val minDim = size.minDimension

                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = minDim / 1.5f,
                    center = Offset(canvasWidth * 0.9f, canvasHeight * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = minDim / 2.5f,
                    center = Offset(canvasWidth * 0.1f, canvasHeight * 0.8f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(R.string.total_balance),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val currencyCode = LocalCurrency.current
                    Text(
                        text = CurrencyUtils.formatAmount(balanceValue, currencyCode),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val currencyCode = LocalCurrency.current

                    // Income Section
                    BalanceSection(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.income),
                        value = CurrencyUtils.formatAmount(totalIncome, currencyCode),
                        icon = Icons.Default.ArrowUpward,
                        iconColor = SuccessGreen
                    )

                    // Expense Section
                    BalanceSection(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.expenses),
                        value = CurrencyUtils.formatAmount(totalExpense, currencyCode),
                        icon = Icons.Default.ArrowDownward,
                        iconColor = ErrorRed
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceSection(
    modifier: Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .background(iconColor.copy(alpha = 0.1f))
            .border(1.dp, iconColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.2f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SummarySmallCard(modifier: Modifier, label: String, value: String, iconBackground: Color, iconRes: Int) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
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
    Transaction(
        id = 1,
        title = "Starbucks",
        amount = 5.50,
        date = System.currentTimeMillis(),
        category = "Food",
        isExpense = true,
        icon = R.drawable.ic_food,
        paymentMode = "UPI"
    ),
    Transaction(
        id = 2,
        title = "Uber",
        amount = 12.00,
        date = System.currentTimeMillis() - 86400000,
        category = "Transport",
        isExpense = true,
        icon = R.drawable.ic_travel,
        paymentMode = "Card"
    ),
    Transaction(
        id = 3,
        title = "Rent",
        amount = 1200.00,
        date = System.currentTimeMillis() - 172800000,
        category = "Bills",
        isExpense = true,
        icon = R.drawable.ic_home,
        paymentMode = "Bank Transfer"
    ),
    Transaction(
        id = 4,
        title = "Whole Foods",
        amount = 84.20,
        date = System.currentTimeMillis() - 259200000,
        category = "Groceries",
        isExpense = true,
        icon = R.drawable.ic_groceries,
        paymentMode = "Cash"
    )
)

@Preview
@Composable
private fun DashboardPreview() {
    MoneyMateTheme {
        DashboardContent(
            transactions = recentTransactions,
            userProfile = UserProfile(name = "John Doe"),
            totalIncome = 5000.0,
            totalExpense = 750.0,
            isLoading = false,
            onAddTransaction = {},
            onEditTransaction = {},
            onDeleteTransaction = {},
            onLongClickTransaction = {},
            onViewAll = {}
        )
    }
}
