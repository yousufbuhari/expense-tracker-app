package com.buhari.moneymate.ui.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buhari.moneymate.R
import com.buhari.moneymate.ui.components.TransactionTypeToggle
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.theme.PrimaryPurple
import java.text.SimpleDateFormat
import java.util.*

data class FilterState(
    val selectedMonths: Set<String> = emptySet(),
    val selectedCategories: Set<String> = emptySet(),
    val selectedCurrencies: Set<String> = emptySet(),
    val selectedPaymentModes: Set<String> = emptySet(),
    val isExpense: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterScreen(
    initialState: FilterState,
    onBack: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    val monthsTab = "Months"
    val categoriesTab = "Categories"
    val currenciesTab = "Currencies"
    val paymentModesTab = "PaymentModes"
    var activeTab by remember { mutableStateOf(monthsTab) }
    var selectedMonths by remember { mutableStateOf(initialState.selectedMonths) }
    var selectedCategories by remember { mutableStateOf(initialState.selectedCategories) }
    var selectedCurrencies by remember { mutableStateOf(initialState.selectedCurrencies) }
    var selectedPaymentModes by remember { mutableStateOf(initialState.selectedPaymentModes) }
    var isExpense by remember { mutableStateOf(initialState.isExpense) }

    val currencies = listOf("INR", "USD", "EUR", "GBP", "AED", "SAR", "SGD", "AUD", "CAD", "JPY", "KWD", "QAR")
    val paymentModes = listOf("Cash", "Card", "UPI", "Bank Transfer")

    val expenseCategories = listOf("Housing", "Food", "Beverages", "Groceries", "Shopping", "Fuel", "Entertainment", "Travel", "Bills", "Finance", "Health", "Sports", "Family", "Pets", "Lending", "Other")
    val incomeCategories = listOf("Salary", "Freelance", "Business", "Investment", "Rental", "Bonus", "Gift", "Refund", "Other")
    
    // Map internal names to string resources for display
    val categoryDisplayNames = mapOf(
        "Housing" to stringResource(R.string.cat_housing),
        "Food" to stringResource(R.string.cat_food),
        "Beverages" to stringResource(R.string.cat_beverages),
        "Groceries" to stringResource(R.string.cat_groceries),
        "Shopping" to stringResource(R.string.cat_shopping),
        "Fuel" to stringResource(R.string.cat_fuel),
        "Entertainment" to stringResource(R.string.cat_entertainment),
        "Travel" to stringResource(R.string.cat_travel),
        "Bills" to stringResource(R.string.cat_bills),
        "Finance" to stringResource(R.string.cat_finance),
        "Health" to stringResource(R.string.cat_health),
        "Sports" to stringResource(R.string.cat_sports),
        "Family" to stringResource(R.string.cat_family),
        "Pets" to stringResource(R.string.cat_pets),
        "Lending" to stringResource(R.string.cat_lending),
        "Salary" to stringResource(R.string.cat_salary),
        "Freelance" to stringResource(R.string.cat_freelance),
        "Business" to stringResource(R.string.cat_business),
        "Investment" to stringResource(R.string.cat_investment),
        "Rental" to stringResource(R.string.cat_rental),
        "Bonus" to stringResource(R.string.cat_bonus),
        "Gift" to stringResource(R.string.cat_gift),
        "Refund" to stringResource(R.string.cat_refund),
        "Other" to stringResource(R.string.cat_other),
        "Cash" to stringResource(R.string.mode_cash),
        "Card" to stringResource(R.string.mode_card),
        "UPI" to stringResource(R.string.mode_upi),
        "Bank Transfer" to stringResource(R.string.mode_bank_transfer)
    )

    val categories = if (isExpense) expenseCategories else incomeCategories

    val months = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        for (i in 0 until 12) {
            list.add(fmt.format(cal.time))
            cal.add(Calendar.MONTH, -1)
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filters), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    TextButton(onClick = {
                        selectedMonths = emptySet()
                        selectedCategories = emptySet()
                        selectedCurrencies = emptySet()
                        selectedPaymentModes = emptySet()
                    }) {
                        Text(stringResource(R.string.clear_all), color = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { onApply(FilterState(selectedMonths, selectedCategories, selectedCurrencies, selectedPaymentModes, isExpense)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text(stringResource(R.string.apply), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TransactionTypeToggle(
                    isExpense = isExpense,
                    onTypeChange = { 
                        isExpense = it 
                        selectedCategories = emptySet() // Reset categories when switching type
                        if (!it && activeTab == paymentModesTab) {
                            activeTab = monthsTab
                        }
                    }
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Left Sidebar
                Column(
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    FilterTabItem(stringResource(R.string.months), activeTab == monthsTab) { activeTab = monthsTab }
                    FilterTabItem(stringResource(R.string.categories), activeTab == categoriesTab) { activeTab = categoriesTab }
                    FilterTabItem(stringResource(R.string.currency), activeTab == currenciesTab) { activeTab = currenciesTab }
                    if (isExpense) {
                        FilterTabItem(stringResource(R.string.payment_mode), activeTab == paymentModesTab) { activeTab = paymentModesTab }
                    }
                }

                // Right Content
                LazyColumn(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    when (activeTab) {
                        monthsTab -> {
                            items(months) { month ->
                                FilterOptionRow(
                                    label = month,
                                    isSelected = selectedMonths.contains(month),
                                    onSelect = {
                                        selectedMonths = if (selectedMonths.contains(month)) {
                                            selectedMonths - month
                                        } else {
                                            selectedMonths + month
                                        }
                                    }
                                )
                            }
                        }
                        categoriesTab -> {
                            items(categories) { category ->
                                FilterOptionRow(
                                    label = categoryDisplayNames[category] ?: category,
                                    isSelected = selectedCategories.contains(category),
                                    onSelect = {
                                        selectedCategories = if (selectedCategories.contains(category)) {
                                            selectedCategories - category
                                        } else {
                                            selectedCategories + category
                                        }
                                    }
                                )
                            }
                        }
                        currenciesTab -> {
                            items(currencies) { currency ->
                                FilterOptionRow(
                                    label = currency,
                                    isSelected = selectedCurrencies.contains(currency),
                                    onSelect = {
                                        selectedCurrencies = if (selectedCurrencies.contains(currency)) {
                                            selectedCurrencies - currency
                                        } else {
                                            selectedCurrencies + currency
                                        }
                                    }
                                )
                            }
                        }
                        paymentModesTab -> {
                            items(paymentModes) { mode ->
                                FilterOptionRow(
                                    label = categoryDisplayNames[mode] ?: mode,
                                    isSelected = selectedPaymentModes.contains(mode),
                                    onSelect = {
                                        selectedPaymentModes = if (selectedPaymentModes.contains(mode)) {
                                            selectedPaymentModes - mode
                                        } else {
                                            selectedPaymentModes + mode
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun FilterOptionRow(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            colors = CheckboxDefaults.colors(checkedColor = PrimaryPurple)
        )
    }
}

@Preview
@Composable
fun TransactionFilterScreenPreview() {
    MoneyMateTheme {
        TransactionFilterScreen(
            initialState = FilterState(),
            onBack = { },
            onApply = { }
        )
    }
}
