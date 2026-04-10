package com.example.pocketplan.ui.budget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.pocketplan.ui.components.*
import com.example.pocketplan.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSetupScreen(
    viewModel: BudgetViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semester Budget Setup", color = TextOnDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextOnDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            SetupView(
                uiState = uiState,
                viewModel = viewModel,
                onSave = {
                    viewModel.saveBudget()
                    onBack()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SetupView(
    uiState: BudgetUiState,
    viewModel: BudgetViewModel,
    onSave: () -> Unit
) {
    var showEditFundsDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryAmountDialog by remember { mutableStateOf(false) }
    var selectedCategoryForEdit by remember { mutableStateOf<com.example.pocketplan.data.model.Category?>(null) }

    val totalAllocation = uiState.categories.sumOf { it.percentage.toInt() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            FundsTotalCard(
                totalAmount = uiState.totalFunds,
                onEditClick = { if (uiState.isEditing) showEditFundsDialog = true }
            )
        }

        item {
            SectionHeader(
                title = "Select Semester Months",
                actionText = "${uiState.selectedMonths.size} Months Selected"
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").forEach { month ->
                    MonthChip(
                        month = month,
                        isSelected = uiState.selectedMonths.contains(month),
                        onClick = { viewModel.toggleMonth(month) }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Allocate to Categories", style = MaterialTheme.typography.titleLarge)
                if (uiState.isEditing) {
                    IconButton(onClick = { showAddCategoryDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
                    }
                }
            }
        }

        items(uiState.categories) { category ->
            CategoryAllocationCard(
                icon = when (category.name.lowercase()) {
                    "rent" -> Icons.Default.Home
                    "tuition" -> Icons.Default.School
                    else -> Icons.Default.Category
                },
                categoryName = category.name,
                allocatedAmount = category.allocatedAmount.toLong(),
                percentage = category.percentage.toInt(),
                onPercentageChange = if (uiState.isEditing) {
                    { newPercent -> viewModel.updateCategoryPercentage(category.id, newPercent) }
                } else null,
                onClick = if (uiState.isEditing) {
                    {
                        selectedCategoryForEdit = category
                        showEditCategoryAmountDialog = true
                    }
                } else null
            )
        }

        if (totalAllocation == 100) {
            item {
                Text(
                    text = "✓ Fully allocated",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            SectionHeader(title = "Monthly Outlook")
        }

        item {
            MonthlyOutlookContent(uiState = uiState)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleEditing() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(if (!uiState.isEditing) "Edit" else "View")
                }
                
                PocketPlanButton(
                    text = "Save Budget",
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = totalAllocation == 100
                )
            }
        }
    }

    if (showEditFundsDialog) {
        var fundsText by remember { mutableStateOf(uiState.totalFunds.toString()) }
        AlertDialog(
            onDismissRequest = { showEditFundsDialog = false },
            title = { Text("Update Total Funds") },
            text = {
                TextField(
                    value = fundsText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) fundsText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    fundsText.toLongOrNull()?.let { viewModel.updateTotalFunds(it) }
                    showEditFundsDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditFundsDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddCategoryDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add New Category") },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) viewModel.addCategory(name, 0)
                    showAddCategoryDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditCategoryAmountDialog && selectedCategoryForEdit != null) {
        var amountText by remember { mutableStateOf(selectedCategoryForEdit!!.allocatedAmount.toLong().toString()) }
        AlertDialog(
            onDismissRequest = { showEditCategoryAmountDialog = false },
            title = { Text("Edit Allocation for ${selectedCategoryForEdit!!.name}") },
            text = {
                TextField(
                    value = amountText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amountText = it },
                    label = { Text("Amount (UGX)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    amountText.toDoubleOrNull()?.let {
                        viewModel.updateCategoryAmount(selectedCategoryForEdit!!.id, it)
                    }
                    showEditCategoryAmountDialog = false
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditCategoryAmountDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MonthlyOutlookContent(uiState: BudgetUiState) {
    val avgMonthly = if (uiState.selectedMonths.isNotEmpty()) {
        uiState.totalFunds / uiState.selectedMonths.size
    } else 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            val annotatedText = buildAnnotatedString {
                append("Your funds are distributed across ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${uiState.selectedMonths.size} months")
                }
                append(". Each month, you'll have an average of ")
                withStyle(style = SpanStyle(color = SecondaryBlue, fontWeight = FontWeight.Bold)) {
                    append("UGX %,d".format(avgMonthly))
                }
                append(" to spend. We've balanced your categories to ensure essentials are covered first.")
            }
            
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                DonutChart(categories = uiState.categories)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val totalAllocation = uiState.categories.sumOf { it.percentage.toInt() }
                    Text(
                        text = "ALLOCATED",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "$totalAllocation%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

@Composable
fun DonutChart(categories: List<com.example.pocketplan.data.model.Category>) {
    Canvas(modifier = Modifier.size(180.dp)) {
        var startAngle = -90f
        categories.forEachIndexed { index, category ->
            val sweepAngle = (category.percentage.toFloat() / 100f) * 360f
            val color = when (index) {
                0 -> AccentTeal
                1 -> PrimaryBlue
                2 -> SecondaryBlue
                else -> PrimaryBlue.copy(alpha = 1f - (index * 0.1f))
            }
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle
        }
    }
}
