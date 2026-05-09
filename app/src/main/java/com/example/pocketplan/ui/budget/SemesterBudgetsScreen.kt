package com.example.pocketplan.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.example.pocketplan.ui.components.*
import com.example.pocketplan.ui.theme.ErrorRed
import com.example.pocketplan.ui.theme.BackgroundLight
import com.example.pocketplan.ui.theme.PrimaryBlue
import com.example.pocketplan.ui.theme.TextPrimary
import com.example.pocketplan.ui.theme.TextSecondary

@Composable
fun SemesterBudgetsScreen(
    viewModel: SemesterBudgetsViewModel,
    onBudgetClick: (budgetId: Long) -> Unit,
    onCreateConfirmed: (budgetId: Long) -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var budgetToDelete by remember { mutableStateOf<BudgetSummary?>(null) }

    Scaffold(
        topBar = {
            PocketPlanTopBar(
                title = "Semester Budgets",
                onNotificationClick = { /* Handle notifications */ },
                onLogoutClick = onLogoutClick
            )
        },
        containerColor = BackgroundLight,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateModal() },
                containerColor = PrimaryBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Budget")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (uiState.budgets.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.budgets) { budget ->
                        BudgetSummaryCard(
                            summary = budget,
                            isExpanded = uiState.expandedBudgetIds.contains(budget.id),
                            onToggleExpand = { viewModel.toggleExpanded(budget.id) },
                            onNavigate = { onBudgetClick(budget.id) },
                            onEditClick = { viewModel.openEditModal(budget) },
                            onDeleteClick = { budgetToDelete = budget },
                            onStatusChange = { categoryId, newStatus ->
                                viewModel.updateCategoryStatus(budget.id, categoryId, newStatus)
                            }
                        )
                    }
                }
            }
        }

        if (budgetToDelete != null) {
            ConfirmationDialog(
                title = "Delete Budget",
                message = "Are you sure you want to delete '${budgetToDelete?.semesterName}'? This action cannot be undone.",
                confirmText = "Delete",
                confirmColor = ErrorRed,
                onConfirm = {
                    budgetToDelete?.let { viewModel.deleteBudget(it.id) }
                    budgetToDelete = null
                },
                onDismiss = { budgetToDelete = null }
            )
        }

        if (uiState.isCreateModalOpen) {
            CreateBudgetModal(
                name = uiState.createName,
                year = uiState.createYear,
                isEditing = uiState.editingBudgetId != null,
                onNameChange = { viewModel.onNameChange(it) },
                onYearChange = { viewModel.onYearChange(it) },
                onDismiss = { viewModel.dismissCreateModal() },
                onConfirm = {
                    viewModel.handleConfirm { newId ->
                        onCreateConfirmed(newId)
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No budgets yet",
            style = MaterialTheme.typography.headlineSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to create your first semester budget",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun CreateBudgetModal(
    name: String,
    year: String,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Semester Budget" else "New Semester Budget",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("e.g. Year 1, Semester 1 (2026)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isEditing) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = onYearChange,
                        label = { Text("Year (e.g. 2026)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            PocketPlanButton(
                text = if (isEditing) "Update Budget" else "Proceed to Setup",
                onClick = onConfirm,
                enabled = name.isNotBlank()
            )
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
