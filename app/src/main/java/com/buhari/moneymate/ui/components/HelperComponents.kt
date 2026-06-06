package com.buhari.moneymate.ui.components

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.buhari.moneymate.R
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
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
                CategoryItem("Freelance", R.drawable.ic_freelance),
                CategoryItem("Business", R.drawable.ic_business),
                CategoryItem("Investment", R.drawable.ic_investment),
                CategoryItem("Rental", R.drawable.ic_rental),
                CategoryItem("Bonus", R.drawable.ic_bonus),
                CategoryItem("Gift", R.drawable.ic_gift),
                CategoryItem("Refund", R.drawable.ic_refund),
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
 * Helper to get category icon resource based on category name.
 * Storing resource IDs in DB is unstable across builds, so we resolve them at runtime.
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
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault()) }
    val dateString = dateFormatter.format(Date(transaction.date))
    val amountString = (if (transaction.isExpense) "-" else "+") + String.format(Locale.getDefault(), "₹%.2f", transaction.amount)

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
                        Icon(Icons.Default.Edit, "Edit", tint = Color.White)
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
                        Icon(Icons.Default.Delete, "Delete", tint = Color.White)
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
                    Text(
                        text = transaction.title,
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
                        text = "Edit",
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
