package com.example.pocketplan.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pocketplan.data.model.Category
import com.example.pocketplan.data.model.CategoryStatus
import com.example.pocketplan.ui.budget.BudgetSummary
import com.example.pocketplan.ui.theme.*
import com.example.pocketplan.utils.CurrencyUtils
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketplan.ui.navigation.Screen

/**
 * 1. PocketPlanTopBar
 * Dark navy background, budget icon, title, and notification bell.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketPlanTopBar(
    title: String,
    onNotificationClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextOnDark,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = "App Icon",
                tint = TextOnDark,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = TextOnDark
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryBlue
        )
    )
}

/**
 * 2. FundsTotalCard
 * Displays the total semester funds with an edit button.
 */
@Composable
fun FundsTotalCard(
    totalAmount: Long,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TOTAL SEMESTER FUNDS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "UGX",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextOnDark
                )
                Text(
                    text = "%,d".format(totalAmount),
                    style = MaterialTheme.typography.displayMedium,
                    color = TextOnDark
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Funds",
                    tint = TextOnDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 3. MonthChip
 * Selectable pill-shaped chip for months.
 */
@Composable
fun MonthChip(
    month: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) ChipSelected else ChipUnselected
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = month,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) TextOnDark else TextPrimary,
                fontWeight = FontWeight.Medium
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TextOnDark,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * 4. CategoryAllocationCard
 * Shows category allocation with a progress bar.
 */
@Composable
fun CategoryAllocationCard(
    icon: ImageVector,
    categoryName: String,
    allocatedAmount: Long,
    percentage: Int,
    onPercentageChange: ((Int) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundLight,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundLight
                ) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = categoryName, style = MaterialTheme.typography.titleLarge)
            Text(
                text = "UGX %,d".format(allocatedAmount),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (onPercentageChange != null) {
                Slider(
                    value = percentage.toFloat(),
                    onValueChange = { onPercentageChange(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlue,
                        activeTrackColor = SecondaryBlue,
                        inactiveTrackColor = ChipUnselected
                    )
                )
            } else {
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = SecondaryBlue,
                    trackColor = ChipUnselected,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * 5. GoalCard
 * Visualizes a goal with a status badge and circular progress.
 */
@Composable
fun GoalCard(
    goalName: String,
    targetAmount: Long,
    dueDate: String,
    progressPercent: Int,
    status: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (status == "PROTECTED") SuccessGreen.copy(alpha = 0.1f) else SecondaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (status == "PROTECTED") SuccessGreen else SecondaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = goalName, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "UGX %,d".format(targetAmount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Due $dueDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                Canvas(modifier = Modifier.size(60.dp)) {
                    drawArc(
                        color = ChipUnselected,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = if (status == "PROTECTED") SuccessGreen else SecondaryBlue,
                        startAngle = -90f,
                        sweepAngle = (progressPercent / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 6. ExpenseListItem
 * Single row item for expenses.
 */
@Composable
fun ExpenseListItem(
    categoryIcon: ImageVector,
    expenseName: String,
    categoryTag: String,
    timeLabel: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = BackgroundLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = categoryIcon, contentDescription = null, tint = PrimaryBlue)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = expenseName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ChipUnselected,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = categoryTag,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(text = timeLabel, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        Text(
            text = "-UGX %,d".format(amount),
            style = MaterialTheme.typography.bodyLarge,
            color = ErrorRed,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 7. PocketPlanButton
 * Primary action button for the app.
 */
@Composable
fun PocketPlanButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = TextOnDark,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f),
            disabledContentColor = TextOnDark.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

/**
 * 8. SectionHeader
 * Header for different sections with optional action text.
 */
@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

/**
 * 9. BudgetSummaryCard
 * Displays a summary of a semester budget with expand/collapse functionality.
 */
@Composable
fun BudgetSummaryCard(
    summary: BudgetSummary,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onNavigate: () -> Unit,
    onStatusChange: (categoryId: String, newStatus: CategoryStatus) -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f, label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate() }
                ) {
                    Text(
                        text = summary.semesterName,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${CurrencyUtils.formatToUGX(summary.totalFunds.toDouble())}  ·  ${summary.monthCount} months",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (summary.isFullyAllocated) SuccessGreen else WarningOrange,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (summary.isFullyAllocated) "✓ Fully Allocated" else "Incomplete",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Created ${summary.createdDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.rotate(rotationState)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = ChipUnselected
                    )
                    
                    Text(
                        text = "CATEGORY ALLOCATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    summary.categories.forEach { category ->
                        CategoryStatusRow(
                            category = category,
                            onStatusChange = { newStatus ->
                                onStatusChange(category.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 10. CategoryStatusRow
 * Displays category name, amount, and status chip in a row.
 */
@Composable
fun CategoryStatusRow(
    category: Category,
    onStatusChange: (CategoryStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = BackgroundLight,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (category.icon) {
                        "home" -> Icons.Default.Home
                        "school" -> Icons.Default.School
                        else -> Icons.Default.ShoppingCart
                    },
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = CurrencyUtils.formatToUGX(category.allocatedAmount.toDouble()),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        
        StatusChip(
            status = category.status,
            onClick = {
                val nextStatus = when (category.status) {
                    CategoryStatus.PENDING -> CategoryStatus.IN_PROGRESS
                    CategoryStatus.IN_PROGRESS -> CategoryStatus.COMPLETED
                    CategoryStatus.COMPLETED -> CategoryStatus.PENDING
                }
                onStatusChange(nextStatus)
            }
        )
    }
}

/**
 * 11. StatusChip
 * Clickable pill-shaped chip showing category status.
 */
@Composable
fun StatusChip(status: CategoryStatus, onClick: () -> Unit) {
    val backgroundColor = when (status) {
        CategoryStatus.PENDING -> ChipUnselected
        CategoryStatus.IN_PROGRESS -> SecondaryBlue.copy(alpha = 0.1f)
        CategoryStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.1f)
    }
    
    val contentColor = when (status) {
        CategoryStatus.PENDING -> TextSecondary
        CategoryStatus.IN_PROGRESS -> SecondaryBlue
        CategoryStatus.COMPLETED -> SuccessGreen
    }
    
    val label = when (status) {
        CategoryStatus.PENDING -> "Pending"
        CategoryStatus.IN_PROGRESS -> "In Progress"
        CategoryStatus.COMPLETED -> "Completed"
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(contentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

/**
 * 12. PocketPlanBottomBar
 * Reusable bottom navigation bar that is docked to the bottom of the screen.
 */
@Composable
fun PocketPlanBottomBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("Budgets", Screen.SemesterBudgets.route, Icons.Default.AccountBalance),
        NavigationItem("Tracking", Screen.Tracking.route, Icons.AutoMirrored.Filled.ReceiptLong),
        NavigationItem("Goals", Screen.Goals.route, Icons.Default.Flag),
        NavigationItem("Insights", Screen.Insights.route, Icons.Default.BarChart)
    )

    Surface(
        color = Color.White,
        tonalElevation = 0.dp
    ) {
        Column {
            HorizontalDivider(color = ChipUnselected.copy(alpha = 0.5f), thickness = 1.dp)
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val isSelected = currentRoute == item.route || (item.route == Screen.SemesterBudgets.route && currentRoute?.startsWith("budget_setup") == true)
                    
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = SecondaryBlue.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    }
}

private data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)
