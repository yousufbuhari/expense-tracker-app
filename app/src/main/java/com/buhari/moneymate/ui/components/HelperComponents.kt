package com.buhari.moneymate.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.buhari.moneymate.R
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

enum class DragAnchors {
    Start,
    End,
}

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
                    .background(if (isExpense) PrimaryPurple else Color.Transparent)
                    .clickable { onTypeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.expenses),
                    color = if (isExpense) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isExpense) PrimaryPurple else Color.Transparent)
                    .clickable { onTypeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.income),
                    color = if (!isExpense) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

data class CategoryItem(val name: String, val labelRes: Int, val iconRes: Int)

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
                CategoryItem("Housing", R.string.cat_housing, R.drawable.ic_home),
                CategoryItem("Food", R.string.cat_food, R.drawable.ic_food),
                CategoryItem("Beverages", R.string.cat_beverages, R.drawable.ic_beverages),
                CategoryItem("Groceries", R.string.cat_groceries, R.drawable.ic_groceries),
                CategoryItem("Shopping", R.string.cat_shopping, R.drawable.ic_shop),
                CategoryItem("Fuel", R.string.cat_fuel, R.drawable.ic_fuel),
                CategoryItem("Entertainment", R.string.cat_entertainment, R.drawable.ic_movies),
                CategoryItem("Travel", R.string.cat_travel, R.drawable.ic_travel),
                CategoryItem("Bills", R.string.cat_bills, R.drawable.ic_bills),
                CategoryItem("Finance", R.string.cat_finance, R.drawable.ic_rupee),
                CategoryItem("Health", R.string.cat_health, R.drawable.ic_health),
                CategoryItem("Sports", R.string.cat_sports, R.drawable.ic_sports),
                CategoryItem("Family", R.string.cat_family, R.drawable.ic_family),
                CategoryItem("Pets", R.string.cat_pets, R.drawable.ic_pets),
                CategoryItem("Lending", R.string.cat_lending, R.drawable.ic_lending),
                CategoryItem("Other", R.string.cat_other, R.drawable.ic_other)
            )
        } else {
            listOf(
                CategoryItem("Salary", R.string.cat_salary, R.drawable.ic_salary),
                CategoryItem("Freelance", R.string.cat_freelance, R.drawable.ic_freelance),
                CategoryItem("Business", R.string.cat_business, R.drawable.ic_business),
                CategoryItem("Investment", R.string.cat_investment, R.drawable.ic_investment),
                CategoryItem("Rental", R.string.cat_rental, R.drawable.ic_rental),
                CategoryItem("Bonus", R.string.cat_bonus, R.drawable.ic_bonus),
                CategoryItem("Gift", R.string.cat_gift, R.drawable.ic_gift),
                CategoryItem("Refund", R.string.cat_refund, R.drawable.ic_refund),
                CategoryItem("Other", R.string.cat_other, R.drawable.ic_other)
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
                        contentDescription = stringResource(category.labelRes),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    stringResource(category.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
    val today = remember { Calendar.getInstance() }
    var currentMonth by remember { mutableStateOf(selectedDate.clone() as Calendar) }

    val isNextMonthInFuture = currentMonth.get(Calendar.YEAR) > today.get(Calendar.YEAR) ||
            (currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    currentMonth.get(Calendar.MONTH) >= today.get(Calendar.MONTH))
    
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
            
            IconButton(
                onClick = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                },
                enabled = !isNextMonthInFuture
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isNextMonthInFuture) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else LocalContentColor.current
                )
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
                            val isFuture = currentMonth.get(Calendar.YEAR) > today.get(Calendar.YEAR) ||
                                    (currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR) && currentMonth.get(Calendar.MONTH) > today.get(Calendar.MONTH)) ||
                                    (currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR) && currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) && day > today.get(Calendar.DAY_OF_MONTH))

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
                                        indication = null,
                                        enabled = !isFuture
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
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        isFuture -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
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

/**
 * Helper to get category name resource based on internal category name.
 */
fun getCategoryNameRes(category: String): Int {
    return when (category) {
        "Housing" -> R.string.cat_housing
        "Food" -> R.string.cat_food
        "Beverages" -> R.string.cat_beverages
        "Groceries" -> R.string.cat_groceries
        "Shopping" -> R.string.cat_shopping
        "Fuel" -> R.string.cat_fuel
        "Entertainment" -> R.string.cat_entertainment
        "Travel" -> R.string.cat_travel
        "Bills" -> R.string.cat_bills
        "Finance" -> R.string.cat_finance
        "Health" -> R.string.cat_health
        "Sports" -> R.string.cat_sports
        "Family" -> R.string.cat_family
        "Pets" -> R.string.cat_pets
        "Lending" -> R.string.cat_lending
        "Salary" -> R.string.cat_salary
        "Freelance" -> R.string.cat_freelance
        "Business" -> R.string.cat_business
        "Investment" -> R.string.cat_investment
        "Rental" -> R.string.cat_rental
        "Bonus" -> R.string.cat_bonus
        "Gift" -> R.string.cat_gift
        "Refund" -> R.string.cat_refund
        else -> R.string.cat_other
    }
}

/**
 * Helper to get category icon resource based on category name.
 */
fun getCategoryIcon(category: String, isExpense: Boolean): Int {
    return if (isExpense) {
        when (category) {
            "Housing" -> R.drawable.ic_home
            "Food" -> R.drawable.ic_food
            "Beverages" -> R.drawable.ic_beverages
            "Groceries" -> R.drawable.ic_groceries
            "Shopping" -> R.drawable.ic_shop
            "Fuel" -> R.drawable.ic_fuel
            "Entertainment" -> R.drawable.ic_movies
            "Travel" -> R.drawable.ic_travel
            "Bills" -> R.drawable.ic_bills
            "Finance" -> R.drawable.ic_rupee
            "Health" -> R.drawable.ic_health
            "Sports" -> R.drawable.ic_sports
            "Family" -> R.drawable.ic_family
            "Pets" -> R.drawable.ic_pets
            "Lending" -> R.drawable.ic_lending
            else -> R.drawable.ic_other
        }
    } else {
        when (category) {
            "Salary" -> R.drawable.ic_salary
            "Freelance" -> R.drawable.ic_freelance
            "Business" -> R.drawable.ic_business
            "Investment" -> R.drawable.ic_investment
            "Rental" -> R.drawable.ic_rental
            "Bonus" -> R.drawable.ic_bonus
            "Gift" -> R.drawable.ic_gift
            "Refund" -> R.drawable.ic_refund
            else -> R.drawable.ic_other
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onEdit: (Transaction) -> Unit = {},
    onDelete: (Transaction) -> Unit = {}
) {
    val datePart = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(transaction.date)) }
    val timePart = remember { SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(transaction.date)) }
    val dateString = "$datePart ${stringResource(R.string.at)} $timePart"
    val amountString = (if (transaction.isExpense) "-" else "+") + stringResource(R.string.amount_format, transaction.amount)

    val density = LocalDensity.current
    val actionWidth = 160.dp
    val actionWidthPx = with(density) { actionWidth.toPx() }

    val anchors = remember {
        DraggableAnchors {
            DragAnchors.Start at 0f
            DragAnchors.End at -actionWidthPx
        }
    }

    val snapAnimationSpec = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 400f
    )
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec
        )
    }
    
    val scope = rememberCoroutineScope()

    val isDark = isSystemInDarkTheme()
    val editColor = if (isDark) EditBlueDark else EditBlue
    val deleteColor = if (isDark) ErrorRed else ErrorRed 

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 1.dp)
    ) {
        val currentOffset = state.offset.takeIf { !it.isNaN() } ?: 0f

        if (currentOffset < -1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Edit Button (Blue)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp)
                            .background(editColor)
                            .clickable { 
                                scope.launch { state.animateTo(DragAnchors.Start) }
                                onEdit(transaction) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, stringResource(R.string.edit), tint = Color.White)
                    }
                    
                    // Delete Button (Red)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp)
                            .background(deleteColor)
                            .clickable { 
                                scope.launch { state.animateTo(DragAnchors.Start) }
                                onDelete(transaction) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, stringResource(R.string.delete), tint = Color.White)
                    }
                }
            }
        }

        // Foreground layer: swipeable card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = currentOffset.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                bottomStart = 20.dp,
                topEnd = if (currentOffset < -1f) 0.dp else 20.dp,
                bottomEnd = if (currentOffset < -1f) 0.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (currentOffset < -1f)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
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
                        painter = painterResource(id = getCategoryIcon(transaction.category, transaction.isExpense)),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val displayTitle = if (transaction.title in listOf("Housing", "Food", "Beverages", "Groceries", "Shopping", "Fuel", "Entertainment", "Travel", "Bills", "Finance", "Health", "Sports", "Family", "Pets", "Lending", "Salary", "Freelance", "Business", "Investment", "Rental", "Bonus", "Gift", "Refund", "Other")) {
                        stringResource(getCategoryNameRes(transaction.title))
                    } else {
                        transaction.title
                    }
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
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
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: Int,
    isDestructive: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDestructive) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isDestructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProfileCard(
    userName: String,
    profileImage: String?,
    onEditClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            TextButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.edit),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(68.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (profileImage.isNullOrEmpty()) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_profile),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            AsyncImage(
                                model = profileImage,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            letterSpacing = 1.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun LanguageSelectionContent(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        Triple("en", stringResource(R.string.english), "E"),
        Triple("ta", stringResource(R.string.tamil), "த"),
        Triple("ml", stringResource(R.string.malayalam), "മ"),
        Triple("te", stringResource(R.string.telugu), "తె"),
        Triple("kn", stringResource(R.string.kannada), "ಕ"),
        Triple("hi", stringResource(R.string.hindi), "हि")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.select_language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        languages.forEach { (code, name, icon) ->
            Surface(
                onClick = { onLanguageSelected(code) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (currentLanguage == code) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = icon,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (currentLanguage == code) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )

                    RadioButton(
                        selected = currentLanguage == code,
                        onClick = { onLanguageSelected(code) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun EditProfileContent(
    currentName: String,
    currentImage: String?,
    onSave: (String, String?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedImageUri by remember { mutableStateOf(currentImage) }
    var showOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it.toString()
        }
    }

    val tempUri = remember {
        val file = File(context.cacheDir, "temp_image.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempUri.toString()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.edit_profile),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image with Edit Action
        Box(
            modifier = Modifier
                .size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { showOptions = true },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                if (selectedImageUri.isNullOrEmpty()) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            // Edit Overlay Button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(32.dp)
                    .clickable { showOptions = true },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_image),
                    modifier = Modifier.padding(7.dp),
                    tint = Color.White
                )
            }
        }

        if (showOptions) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        showOptions = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.gallery))
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                cameraLauncher.launch(tempUri)
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                        showOptions = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.camera))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.profile_name)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, selectedImageUri) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun ThemeSelectionContent(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themes = mapOf(
        "Use device theme" to R.string.use_device_theme,
        "Light" to R.string.light,
        "Dark" to R.string.dark
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 2.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.choose_theme),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        themes.forEach { (themeKey, themeRes) ->
            val themeName = stringResource(themeRes)
            Surface(
                onClick = { onThemeSelected(themeKey) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (currentTheme == themeKey) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = themeName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (currentTheme == themeKey) FontWeight.Bold else FontWeight.Normal
                    )
                    RadioButton(
                        selected = currentTheme == themeKey,
                        onClick = { onThemeSelected(themeKey) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
