package com.example.pocketplan.ui.goals

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketplan.ui.components.GoalCard
import com.example.pocketplan.ui.components.PocketPlanButton
import com.example.pocketplan.ui.components.PocketPlanTopBar
import com.example.pocketplan.ui.theme.PrimaryBlue
import com.example.pocketplan.ui.theme.SecondaryBlue
import com.example.pocketplan.ui.theme.SurfaceWhite
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
            ExtendedFloatingActionButton(
                onClick = { viewModel.onAddGoalClick() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("ADD NEW GOAL") },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            )
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
                PortfolioHealthCard(uiState.portfolioHealthPercent)
            }

            item {
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

            items(uiState.goals) { goal ->
                GoalCard(
                    goalName = goal.name,
                    targetAmount = goal.targetAmount.toLong(),
                    dueDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(goal.dueDate)),
                    progressPercent = if (goal.status == "Protected") 100 else 45, // Placeholder logic
                    status = goal.status.uppercase()
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (uiState.isAddGoalSheetOpen) {
        AddGoalBottomSheet(
            onDismiss = { viewModel.onDismissSheet() },
            onSave = { name, amount, date ->
                viewModel.saveGoal(name, amount, date)
            },
            onImagePick = { viewModel.onImagePicked(it) },
            attachedImageUri = uiState.attachedImageUri
        )
    }
}

@Composable
fun PortfolioHealthCard(percent: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "You've successfully secured $percent% of your semester's critical expenses",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = SecondaryBlue,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, Double, Long) -> Unit,
    onImagePick: (Uri?) -> Unit,
    attachedImageUri: Uri?
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePick(uri)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add New Goal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

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

            Button(
                onClick = { photoPickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (attachedImageUri != null) "Image Attached" else "Attach Proof/Reference Photo")
            }

            PocketPlanButton(
                text = "Save Goal",
                onClick = {
                    onSave(name, amount.toDoubleOrNull() ?: 0.0, dueDate)
                },
                enabled = name.isNotBlank() && amount.isNotBlank()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

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
