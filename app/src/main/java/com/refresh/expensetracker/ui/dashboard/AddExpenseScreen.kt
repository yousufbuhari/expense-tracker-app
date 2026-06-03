package com.refresh.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import com.refresh.expensetracker.ui.theme.ExpenseTrackerTheme
import com.refresh.expensetracker.ui.theme.PrimaryPurple
import com.refresh.expensetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(onClose: () -> Unit = {}) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedTime by remember { mutableStateOf(Calendar.getInstance()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Done") }
            },
            text = {
                CompactCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }
        )
    }

    if (showTimePicker) {
        var hourInput by remember { mutableStateOf(String.format("%02d", if (selectedTime.get(Calendar.HOUR_OF_DAY) % 12 == 0) 12 else selectedTime.get(Calendar.HOUR_OF_DAY) % 12)) }
        var minuteInput by remember { mutableStateOf(String.format("%02d", selectedTime.get(Calendar.MINUTE))) }
        var isAm by remember { mutableStateOf(selectedTime.get(Calendar.HOUR_OF_DAY) < 12) }

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val h = hourInput.toIntOrNull() ?: 12
                    val m = minuteInput.toIntOrNull() ?: 0
                    val finalHour = when {
                        isAm && h == 12 -> 0
                        isAm -> h
                        !isAm && h == 12 -> 12
                        else -> h + 12
                    }
                    val cal = (selectedTime.clone() as Calendar).apply {
                        set(Calendar.HOUR_OF_DAY, finalHour)
                        set(Calendar.MINUTE, m)
                    }
                    selectedTime = cal
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Select Time (12h)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            OutlinedTextField(
                                value = hourInput,
                                onValueChange = {
                                    if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                        val h = it.toIntOrNull()
                                        if (h == null || h in 0..12) hourInput = it
                                    }
                                },
                                modifier = Modifier.width(70.dp),
                                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Text("Hour", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                        }
                        Text(":", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 20.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            OutlinedTextField(
                                value = minuteInput,
                                onValueChange = {
                                    if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                        val m = it.toIntOrNull()
                                        if (m == null || m in 0..59) minuteInput = it
                                    }
                                },
                                modifier = Modifier.width(70.dp),
                                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Text("Minute", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(bottom = 20.dp)) {
                            Surface(
                                onClick = { isAm = true },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isAm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(width = 50.dp, height = 35.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("am", style = MaterialTheme.typography.labelSmall, color = if (isAm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                onClick = { isAm = false },
                                shape = RoundedCornerShape(8.dp),
                                color = if (!isAm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(width = 50.dp, height = 35.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("pm", style = MaterialTheme.typography.labelSmall, color = if (!isAm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.add_expense), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(stringResource(R.string.amount), fontSize = 14.sp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_rupee),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = amount,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) {
                            amount = it
                        }
                    },
                    textStyle = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .defaultMinSize(minWidth = 50.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            if (amount.isEmpty()) {
                                Text(
                                    text = "0.00",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            
            HorizontalDivider(modifier = Modifier.width(200.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.what_did_you_spend_on)) },
                placeholder = { Text(stringResource(R.string.e_g_weekly_groceries)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                stringResource(R.string.category),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CategoryGrid(selectedCategory) { selectedCategory = it }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val isToday = Calendar.getInstance().let {
                            it.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                    it.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)
                        }
                        val dateText = if (isToday) {
                            "Today, " + SimpleDateFormat("MMM dd", Locale.getDefault()).format(selectedDate.time)
                        } else {
                            dateFormatter.format(selectedDate.time)
                        }
                        Text(dateText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(timeFormatter.format(selectedTime.time), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.save_expense), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryGrid(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = remember {
        listOf(
            CategoryItem("Food", R.drawable.ic_food),
            CategoryItem("Groceries", R.drawable.ic_groceries),
            CategoryItem("Shopping", R.drawable.ic_shop),
            CategoryItem("Fuel", R.drawable.ic_fuel),
            CategoryItem("Movies", R.drawable.ic_movies),
            CategoryItem("Travel", R.drawable.ic_travel),
            CategoryItem("Bills", R.drawable.ic_receipt),
            CategoryItem("Finance", R.drawable.ic_rupee),
            CategoryItem("Health", R.drawable.ic_health),
            CategoryItem("Sports", R.drawable.ic_sports),
            CategoryItem("Family", R.drawable.ic_family),
            CategoryItem("Other", R.drawable.ic_other)
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.name == selectedCategory
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategorySelected(category.name) }
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
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable {
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

data class CategoryItem(val name: String, val iconRes: Int)

@Preview
@Composable
fun AddExpensePreview() {
    ExpenseTrackerTheme {
        AddExpenseScreen()
    }
}