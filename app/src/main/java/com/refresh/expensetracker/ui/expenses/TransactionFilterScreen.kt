package com.refresh.expensetracker.ui.expenses

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
import com.refresh.expensetracker.R
import com.refresh.expensetracker.ui.components.TransactionTypeToggle
import com.refresh.expensetracker.ui.theme.ExpenseTrackerTheme
import com.refresh.expensetracker.ui.theme.PrimaryPurple
import java.text.SimpleDateFormat
import java.util.*

data class FilterState(
    val selectedMonths: Set<String> = emptySet(),
    val selectedCategories: Set<String> = emptySet(),
    val isExpense: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterScreen(
    initialState: FilterState,
    onBack: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var activeTab by remember { mutableStateOf("Months") }
    var selectedMonths by remember { mutableStateOf(initialState.selectedMonths) }
    var selectedCategories by remember { mutableStateOf(initialState.selectedCategories) }
    var isExpense by remember { mutableStateOf(initialState.isExpense) }

    val expenseCategories = listOf("Housing", "Food", "Beverages", "Groceries", "Shopping", "Fuel", "Entertainment", "Travel", "Bills", "Finance", "Health", "Sports", "Family", "Pets", "Lending", "Other")
    val incomeCategories = listOf("Salary", "Freelance", "Business", "Investment", "Rental", "Bonus", "Gift", "Refund", "Other")
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
                title = { Text(stringResource(R.string.filters), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        selectedMonths = emptySet()
                        selectedCategories = emptySet()
                    }) {
                        Text(stringResource(R.string.clear_all), color = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { onApply(FilterState(selectedMonths, selectedCategories, isExpense)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text(stringResource(R.string.apply), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    FilterTabItem(stringResource(R.string.months), activeTab == "Months") { activeTab = "Months" }
                    FilterTabItem(stringResource(R.string.categories), activeTab == "Categories") { activeTab = "Categories" }
                }

                // Right Content
                LazyColumn(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    if (activeTab == "Months") {
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
                    } else {
                        items(categories) { category ->
                            FilterOptionRow(
                                label = category,
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
            fontSize = 14.sp,
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
    ExpenseTrackerTheme {
        TransactionFilterScreen(
            initialState = FilterState(),
            onBack = { },
            onApply = { }
        )
    }
}
