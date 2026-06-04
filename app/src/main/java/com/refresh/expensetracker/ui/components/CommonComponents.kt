package com.refresh.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refresh.expensetracker.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * A segmented toggle component to switch between Transaction types (Expenses/Income).
 */
@Composable
fun TransactionTypeToggle(
    isExpense: Boolean,
    onTypeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isExpense) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTypeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.expenses),
                    color = if (isExpense) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isExpense) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTypeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.income),
                    color = if (!isExpense) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
            }
        }
    }
}

data class CategoryItem(val name: String, val iconRes: Int)

/**
 * A grid of categories that changes based on whether it's an expense or income.
 */
@Composable
fun CategoryGrid(
    selectedCategory: String,
    isExpense: Boolean,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember(isExpense) {
        if (isExpense) {
            listOf(
                CategoryItem("Housing", R.drawable.ic_home),
                CategoryItem("Food", R.drawable.ic_food),
                CategoryItem("Beverages", R.drawable.ic_beverages),
                CategoryItem("Groceries", R.drawable.ic_groceries),
                CategoryItem("Shopping", R.drawable.ic_shop),
                CategoryItem("Fuel", R.drawable.ic_fuel),
                CategoryItem("Entertainment", R.drawable.ic_movies),
                CategoryItem("Travel", R.drawable.ic_travel),
                CategoryItem("Bills", R.drawable.ic_bills),
                CategoryItem("Finance", R.drawable.ic_rupee),
                CategoryItem("Health", R.drawable.ic_health),
                CategoryItem("Sports", R.drawable.ic_sports),
                CategoryItem("Family", R.drawable.ic_family),
                CategoryItem("Pets", R.drawable.ic_pets),
                CategoryItem("Lending", R.drawable.ic_lending),
                CategoryItem("Other", R.drawable.ic_other)
            )
        } else {
            listOf(
                CategoryItem("Salary", R.drawable.ic_salary),
                CategoryItem("Side Hustle", R.drawable.ic_side_hustle),
                CategoryItem("Gift", R.drawable.ic_gift),
                CategoryItem("Rental", R.drawable.ic_rental),
                CategoryItem("Investment", R.drawable.ic_increase),
                CategoryItem("Other", R.drawable.ic_other)
            )
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(categories) { category ->
            val isSelected = category.name == selectedCategory
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCategorySelected(category.name) }
            ) {
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer)
                        .then(
                            if (isSelected) Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(12.dp)
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = category.iconRes),
                        contentDescription = category.name,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    category.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * A custom compact calendar component for date selection.
 */
@Composable
fun CompactCalendar(
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    var currentMonth by remember { mutableStateOf(selectedDate.clone() as Calendar) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
            }
            
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val calendar = (currentMonth.clone() as Calendar).apply { 
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val prevDays = firstDayOfWeek - 1
        
        Column {
            for (i in 0 until 6) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val cellIndex = i * 7 + j
                        val day = cellIndex - prevDays + 1
                        if (day in 1..daysInMonth) {
                            val isSelected = selectedDate.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                                    selectedDate.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                    selectedDate.get(Calendar.DAY_OF_MONTH) == day
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        val newDate = (currentMonth.clone() as Calendar).apply {
                                            set(Calendar.YEAR, currentMonth.get(Calendar.YEAR))
                                            set(Calendar.MONTH, currentMonth.get(Calendar.MONTH))
                                            set(Calendar.DAY_OF_MONTH, day)
                                        }
                                        onDateSelected(newDate)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
