package com.example.pocketplan.ui.tracking

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketplan.ui.components.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pocketplan.ui.theme.*

@Composable
fun ExpenseTrackingScreen(
    viewModel: ExpenseTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PocketPlanTopBar(
                title = "All Expenses",
                onNotificationClick = { }
            )
        },
        containerColor = BackgroundLight,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.resetForm()
                    showAddDialog = true
                },
                containerColor = PrimaryBlue,
                contentColor = TextOnDark,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AllExpensesList(state.recentExpenses)
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showAddDialog = false },
            onSaveSuccess = { }
        )
    }
}

private const val COLLAPSED_ITEM_COUNT = 5

/**
 * Full list of all saved expenses. Renders an empty-state block if none exist yet,
 * and shows a "Show More" / "Show Less" toggle once the list exceeds [COLLAPSED_ITEM_COUNT].
 */
@Composable
fun AllExpensesList(expenses: List<Expense>) {

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        SectionHeader(
            title = "All Expenses"
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (expenses.isEmpty()) {
            EmptyExpensesState()
        } else {
            Text(
                text = "${expenses.size} recorded",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val showToggle = expenses.size > COLLAPSED_ITEM_COUNT
            val visibleExpenses = if (showToggle && !expanded) {
                expenses.take(COLLAPSED_ITEM_COUNT)
            } else {
                expenses
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 96.dp) // clear the FAB
            ) {
                items(visibleExpenses) { expense ->
                    val icon: ImageVector = iconForCategory(expense.category)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceWhite,
                        tonalElevation = 0.dp
                    ) {
                        ExpenseListItem(
                            categoryIcon = icon,
                            expenseName = expense.name,
                            categoryTag = expense.category,
                            timeLabel = expense.date.ifBlank { "Today" },
                            amount = expense.amount,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (showToggle) {
                    item {
                        ShowMoreButton(
                            expanded = expanded,
                            hiddenCount = expenses.size - COLLAPSED_ITEM_COUNT,
                            onToggle = { expanded = !expanded }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowMoreButton(
    expanded: Boolean,
    hiddenCount: Int,
    onToggle: () -> Unit
) {
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedButton(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryBlue
        )
    ) {
        Text(
            text = if (expanded) "Show Less" else "Show More ($hiddenCount more)",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyExpensesState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite,
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No expenses yet",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tap the + button to record your first expense",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

/**
 * Popup that hosts the Track Expense form. Closes itself on a successful save.
 */
@Composable
fun AddExpenseDialog(
    state: TrackingUiState,
    viewModel: ExpenseTrackingViewModel,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceWhite,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Track Expense",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AddExpenseForm(
                    state = state,
                    viewModel = viewModel,
                    onSave = {
                        if (viewModel.saveExpense()) {
                            onSaveSuccess()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseForm(
    state: TrackingUiState,
    viewModel: ExpenseTrackingViewModel,
    onSave: () -> Unit
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    // Tap-to-open handling for the Date field.
    var showDatePicker by remember { mutableStateOf(false) }
    val dateInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(dateInteractionSource) {
        dateInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Amount
        OutlinedTextField(
            value = state.amount,
            onValueChange = { viewModel.onAmountChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Amount *") },
            leadingIcon = { Text("UGX") },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = state.amountError != null,
            supportingText = {
                state.amountError?.let {
                    Text(it, color = ErrorRed)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                cursorColor = PrimaryBlue,
                errorBorderColor = ErrorRed,
                errorLabelColor = ErrorRed,
                errorCursorColor = ErrorRed
            )
        )

        // Category Header with Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SELECT CATEGORY *",
                style = MaterialTheme.typography.labelMedium,
                color = if (state.categoryError != null) ErrorRed else TextSecondary
            )
            IconButton(
                onClick = { showAddCategoryDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Custom Category",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            state.categories.forEach { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = {
                        Text(
                            text = category,
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = TextOnDark,
                        containerColor = ChipUnselected,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        state.categoryError?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed
            )
        }

        // Note
        OutlinedTextField(
            value = state.note,
            onValueChange = { viewModel.onNoteChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                cursorColor = PrimaryBlue
            )
        )

        // Date — readOnly, tap to open picker
        OutlinedTextField(
            value = state.date,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Date *") },
            placeholder = { Text("Select a date") },
            readOnly = true,
            interactionSource = dateInteractionSource,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryBlue)
            },
            shape = RoundedCornerShape(12.dp),
            isError = state.dateError != null,
            supportingText = {
                state.dateError?.let {
                    Text(it, color = ErrorRed)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                cursorColor = PrimaryBlue,
                errorBorderColor = ErrorRed,
                errorLabelColor = ErrorRed,
                errorCursorColor = ErrorRed
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Save Button (REUSABLE) — always enabled so the user can tap and see inline errors
        PocketPlanButton(
            text = "Save Expense",
            onClick = onSave
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { newCategory ->
                viewModel.addCategory(newCategory)
                showAddCategoryDialog = false
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDatePicked(millis)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = SurfaceWhite
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryBlue,
                    selectedDayContentColor = TextOnDark,
                    todayDateBorderColor = PrimaryBlue,
                    todayContentColor = PrimaryBlue
                )
            )
        }
    }
}

private fun iconForCategory(category: String): ImageVector = when (category) {
    "Food" -> Icons.Default.Fastfood
    "Transport" -> Icons.Default.DirectionsCar
    "Shopping" -> Icons.Default.ShoppingCart
    else -> Icons.Default.Category
}