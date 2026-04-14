package com.example.pocketplan.ui.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pocketplan.ui.components.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ExpenseTrackingScreen(
    viewModel: ExpenseTrackingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PocketPlanTopBar(
                title = "Track Expense",
                onNotificationClick = { }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            AddExpenseForm(state, viewModel)

            Spacer(modifier = Modifier.height(24.dp))

            RecentExpensesList(state.recentExpenses)
        }
    }
}

@Composable
fun AddExpenseForm(state: TrackingUiState, viewModel: ExpenseTrackingViewModel) {

    val categories = listOf("Food", "Transport", "Shopping", "Misc")

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Amount
        OutlinedTextField(
            value = state.amount,
            onValueChange = { viewModel.onAmountChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Amount") },
            leadingIcon = { Text("UGX") },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Category
        Text(
            "SELECT CATEGORY",
            style = MaterialTheme.typography.labelMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Note
        OutlinedTextField(
            value = state.note,
            onValueChange = { viewModel.onNoteChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note") },
            shape = RoundedCornerShape(12.dp)
        )

        // Date
        OutlinedTextField(
            value = state.date,
            onValueChange = { viewModel.onDateChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Date") },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp)
        )

        // Save Button (REUSABLE)
        PocketPlanButton(
            text = "Save Expense",
            onClick = { viewModel.saveExpense() }
        )
    }
}

@Composable
fun RecentExpensesList(expenses: List<Expense>) {

    Column {

        SectionHeader(
            title = "Recent Expenses",
            actionText = "View All",
            onActionClick = { }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(expenses) { expense ->

                val icon: ImageVector = when (expense.category) {
                    "Food" -> Icons.Default.Fastfood
                    "Transport" -> Icons.Default.DirectionsCar
                    "Shopping" -> Icons.Default.ShoppingCart
                    else -> Icons.Default.Category
                }

                ExpenseListItem(
                    categoryIcon = icon,
                    expenseName = expense.name,
                    categoryTag = expense.category,
                    timeLabel = expense.date,
                    amount = expense.amount
                )
            }
        }
    }
}