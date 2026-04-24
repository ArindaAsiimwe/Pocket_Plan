package com.example.pocketplan.ui.goals

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketplan.ui.components.GoalCard
import com.example.pocketplan.ui.components.PocketPlanButton
import com.example.pocketplan.ui.components.PocketPlanTopBar
import com.example.pocketplan.ui.theme.BackgroundLight
import com.example.pocketplan.ui.theme.PrimaryBlue
import com.example.pocketplan.ui.theme.SecondaryBlue
import com.example.pocketplan.ui.theme.SurfaceWhite
import com.example.pocketplan.ui.theme.TextPrimary
import com.example.pocketplan.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PocketPlanTopBar(title = "Goals", onNotificationClick = {})
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddGoalClick() },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Goal")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Text(
                        text = "Protect your funds",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your critical semester expenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            items(uiState.goals, key = { it.id }) { goal ->
                GoalCard(
                    goalId = goal.id,
                    goalName = goal.name,
                    targetAmount = goal.targetAmount.toLong(),
                    dueDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(goal.dueDate)),
                    progressPercent = if (goal.status == "COMPLETED") 100 else if (goal.status == "IN_PROGRESS") 45 else 0,
                    status = goal.status,
                    attachedImageUri = goal.attachedImageUri,
                    onStatusChange = { newStatus ->
                        viewModel.updateGoalStatus(goal.id, newStatus)
                    },
                    onImagePickWithUri = { uri ->
                        viewModel.updateGoalImage(goal.id, uri)
                    },
                    onImageDelete = {
                        viewModel.deleteGoalImage(goal.id)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (uiState.isAddGoalSheetOpen) {
        AddGoalModal(
            onDismiss = { viewModel.onDismissSheet() },
            onSave = { name, amount, date ->
                viewModel.saveGoal(name, amount, date)
            }
        )
    }
}


@Composable
fun AddGoalModal(
    onDismiss: () -> Unit,
    onSave: (String, Double, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Goal",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Target Amount (UGX)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dueDate)),
                    onValueChange = {},
                    label = { Text("Due Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    }
                )
            }
        },
        confirmButton = {
            PocketPlanButton(
                text = "Save Goal",
                onClick = {
                    onSave(name, amount.toDoubleOrNull() ?: 0.0, dueDate)
                },
                enabled = name.isNotBlank() && amount.isNotBlank()
            )
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis ?: dueDate
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
