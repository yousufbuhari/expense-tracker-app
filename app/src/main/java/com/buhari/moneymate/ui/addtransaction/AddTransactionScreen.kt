package com.buhari.moneymate.ui.addtransaction

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.launch
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.theme.PrimaryPurple
import com.buhari.moneymate.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.ui.components.CategoryGrid
import com.buhari.moneymate.ui.components.CompactCalendar
import com.buhari.moneymate.ui.components.TransactionTypeToggle
import com.buhari.moneymate.ui.components.getCategoryIcon
import com.buhari.moneymate.ui.components.getCategoryNameRes
import com.buhari.moneymate.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Int? = null,
    onClose: () -> Unit = {},
    viewModel: TransactionViewModel = viewModel()
) {
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (isExpense) "Housing" else "Salary") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedTime by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            val transaction = viewModel.getTransactionById(transactionId)
            transaction?.let {
                isExpense = it.isExpense
                amount = it.amount.toString()
                description = it.description ?: it.title
                selectedCategory = it.category
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                selectedDate = cal.clone() as Calendar
                selectedTime = cal.clone() as Calendar
            }
        }
    }

    AddTransactionContent(
        isExpenseInit = isExpense,
        amountInit = amount,
        descriptionInit = description,
        selectedCategoryInit = selectedCategory,
        selectedDateInit = selectedDate,
        selectedTimeInit = selectedTime,
        isEdit = transactionId != null,
        onClose = onClose,
        onSaveExpense = { transaction ->
            if (transactionId != null) {
                viewModel.insertTransaction(transaction.copy(id = transactionId))
            } else {
                viewModel.insertTransaction(transaction)
            }
            onClose()
        }
    )
}

@SuppressLint("LocalContextResourcesRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    isExpenseInit: Boolean = true,
    amountInit: String = "",
    descriptionInit: String = "",
    selectedCategoryInit: String = "Housing",
    selectedDateInit: Calendar = Calendar.getInstance(),
    selectedTimeInit: Calendar = Calendar.getInstance(),
    isEdit: Boolean = false,
    onClose: () -> Unit = {},
    onSaveExpense: (Transaction) -> Unit = {}
) {
    var isExpense by remember(isExpenseInit) { mutableStateOf(isExpenseInit) }
    var amount by remember(amountInit) { mutableStateOf(amountInit) }
    var description by remember(descriptionInit) { mutableStateOf(descriptionInit) }
    var selectedCategory by remember(selectedCategoryInit) { mutableStateOf(selectedCategoryInit) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val res = context.resources

    var selectedDate by remember(selectedDateInit) { mutableStateOf(selectedDateInit) }
    var selectedTime by remember(selectedTimeInit) { mutableStateOf(selectedTimeInit) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val amountErrorMessage = stringResource(R.string.amount_error)
    val wasEdit = remember { isEdit }

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.ok)) }
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
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.select_time_12h),
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
                            Text(stringResource(R.string.hour), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
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
                            Text(stringResource(R.string.minute), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
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
                                    Text(stringResource(R.string.am), style = MaterialTheme.typography.labelSmall, color = if (isAm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    Text(stringResource(R.string.pm), style = MaterialTheme.typography.labelSmall, color = if (!isAm) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
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
            TopAppBar(
                title = {
                    val title = if (wasEdit) {
                        if (isExpense) stringResource(R.string.edit_expense) else stringResource(R.string.edit_income)
                    } else {
                        if (isExpense) stringResource(R.string.add_expense) else stringResource(R.string.add_income)
                    }
                    Text(title, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        if (amountValue > 0) {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = selectedDate.timeInMillis
                                set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
                            }

                            val titleString = description.ifBlank {
                                selectedCategory
                            }

                            val transaction = Transaction(
                                title = titleString,
                                amount = amountValue,
                                date = calendar.timeInMillis,
                                category = selectedCategory,
                                isExpense = isExpense,
                                icon = getCategoryIcon(selectedCategory, isExpense)
                            )
                            onSaveExpense(transaction)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(amountErrorMessage)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    val buttonText = if (isEdit) {
                        stringResource(R.string.update)
                    } else {
                        if (isExpense) stringResource(R.string.save_expense) else stringResource(R.string.save_income)
                    }
                    Text(buttonText, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (!isEdit) {
                TransactionTypeToggle(
                    isExpense = isExpense,
                    onTypeChange = {
                        isExpense = it
                        selectedCategory = if (it) "Housing" else "Salary"
                        amount = ""
                        description = ""
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                stringResource(R.string.amount),
                letterSpacing = 1.sp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_rupee),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
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
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .defaultMinSize(minWidth = 50.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            if (amount.isEmpty()) {
                                Text(
                                    text = stringResource(R.string._0_00),
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (isExpense)(stringResource(R.string.what_did_you_spend_on)) else (stringResource(R.string.what_did_you_earn_from))) },
                placeholder = { Text(if (isExpense)(stringResource(R.string.e_g_weekly_groceries)) else (stringResource(R.string.e_g_salary))) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val isToday = Calendar.getInstance().let {
                            it.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                    it.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)
                        }
                        /* val dateText = if (isToday) {
                            stringResource(R.string.today_format, SimpleDateFormat("MMM dd", Locale.getDefault()).format(selectedDate.time))
                        } else {
                            dateFormatter.format(selectedDate.time)
                        } */

                        val dateText = dateFormatter.format(selectedDate.time)
                        Text(
                            dateText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showTimePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            timeFormatter.format(selectedTime.time),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.category),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CategoryGrid(
                selectedCategory = selectedCategory,
                isExpense = isExpense,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier.heightIn(max = 400.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun AddTransactionPreview() {
    MoneyMateTheme {
        AddTransactionContent()
    }
}