package com.example.pocketplan.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun ExpenseTrackingScreen(
    viewModel: ExpenseTrackingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Track Expense",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        AddExpenseForm(state, viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        RecentExpensesList(state.recentExpenses)
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
            leadingIcon = {
                Text(
                    "UGX",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Category
        Text(
            "SELECT CATEGORY",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(12.dp)
        )

        // Save Button
        Button(
            onClick = { viewModel.saveExpense() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Save Expense",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun RecentExpensesList(expenses: List<Expense>) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Recent Expenses",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "View All",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(expenses) { expense ->
                ExpenseItem(expense)
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {

    val icon = when (expense.category) {
        "Food" -> Icons.Default.Fastfood
        "Transport" -> Icons.Default.DirectionsCar
        "Shopping" -> Icons.Default.ShoppingCart
        else -> Icons.Default.Category
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(expense.name, fontWeight = FontWeight.SemiBold)
                    Text(
                        expense.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        expense.date,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                text = "- UGX ${expense.amount}",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}