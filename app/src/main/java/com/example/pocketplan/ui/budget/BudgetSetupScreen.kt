package com.example.pocketplan.ui.budget

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.pocketplan.ui.components.*
import com.example.pocketplan.ui.theme.*
import com.example.pocketplan.utils.CurrencyUtils
import java.io.File

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
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            FundsTotalCard(
                totalAmount = uiState.totalFunds,
                onEditClick = { if (uiState.isEditing) showEditFundsDialog = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(uiState.categories) { category ->
            CategoryAllocationCard(
                icon = when (category.icon) {
                    "home" -> Icons.Default.Home
                    "school" -> Icons.Default.School
                    else -> Icons.Default.ShoppingCart
                },
                categoryName = category.name,
                allocatedAmount = category.allocatedAmount,
                percentage = category.percentage,
                onPercentageChange = if (uiState.isEditing) { percentage -> 
                    viewModel.updateCategoryPercentage(category.id, percentage) 
                } else null,
                onClick = {
                    if (uiState.isEditing) {
                        selectedCategoryForEdit = category
                        showEditCategoryAmountDialog = true
                    }
                }
            )
            
            CategoryPhotoAttachment(
                categoryId = category.id,
                categoryName = category.name,
                attachedUri = category.attachedImageUri,
                isExpanded = uiState.expandedAttachmentIds.contains(category.id),
                onToggle = { viewModel.toggleAttachmentSection(category.id) },
                onImageSelected = { uri ->
                    viewModel.updateCategoryPhoto(category.id, uri)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Monthly Outlook",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            MonthlyOutlookContent(uiState)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleEditing() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text(if (uiState.isEditing) "Done Editing" else "Edit")
                }
                
                Button(
                    onClick = onSave,
                    enabled = totalAllocation == 100 && uiState.selectedMonths.isNotEmpty(),
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save Budget Plan")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showEditFundsDialog) {
        var textValue by remember { mutableStateOf(uiState.totalFunds.toString()) }
        AlertDialog(
            onDismissRequest = { showEditFundsDialog = false },
            title = { Text("Edit Total Funds") },
            text = {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Amount (UGX)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    textValue.toLongOrNull()?.let { viewModel.updateTotalFunds(it) }
                    showEditFundsDialog = false
                }) { Text("Update") }
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addCategory(name)
                        showAddCategoryDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditCategoryAmountDialog && selectedCategoryForEdit != null) {
        var amountText by remember { mutableStateOf(selectedCategoryForEdit!!.allocatedAmount.toString()) }
        AlertDialog(
            onDismissRequest = { showEditCategoryAmountDialog = false },
            title = { Text("Edit ${selectedCategoryForEdit!!.name} Amount") },
            text = {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
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
fun CategoryPhotoAttachment(
    categoryId: String,
    categoryName: String,
    attachedUri: Uri?,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) onImageSelected(tempCameraUri)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { onImageSelected(it) } }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ChipUnselected, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (attachedUri == null) TextSecondary else SuccessGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (attachedUri == null) "Attach proof photo" else "Photo attached",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (attachedUri == null) TextSecondary else SuccessGreen
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.rotate(rotationState)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 8.dp, top = 4.dp)
                ) {
                    if (attachedUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = attachedUri,
                                contentDescription = "Attached proof for $categoryName",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { onImageSelected(null) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(SurfaceWhite, CircleShape)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    modifier = Modifier.size(16.dp),
                                    tint = TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(SecondaryBlue)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryBlue)
                        ) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Take Photo", style = MaterialTheme.typography.bodySmall)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(SecondaryBlue)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryBlue)
                        ) {
                            Icon(Icons.Outlined.Photo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("From Gallery", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private fun createTempImageUri(context: Context): Uri {
    val fileName = "pocket_plan_photo_${System.currentTimeMillis()}.jpg"
    val file = File(context.cacheDir, fileName)
    
    // Ensure the cache directory exists
    if (!context.cacheDir.exists()) {
        context.cacheDir.mkdirs()
    }
    
    // Create the file if it doesn't exist
    if (!file.exists()) {
        file.createNewFile()
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@Composable
fun MonthlyOutlookContent(uiState: BudgetUiState) {
    val totalAllocation = uiState.categories.sumOf { it.percentage }
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
            val annotatedString = buildAnnotatedString {
                append("Your funds are distributed across ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${uiState.selectedMonths.size} months")
                }
                append(". Each month, you'll have an average of ")
                withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)) {
                    append(CurrencyUtils.formatToUGX(avgMonthly.toDouble()))
                }
                append(" to spend. We've balanced your categories to ensure essentials are covered first.")
            }

            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified 
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    // Background track
                    drawArc(
                        color = ChipUnselected.copy(alpha = 0.3f),
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress
                    drawArc(
                        color = AccentTeal,
                        startAngle = 140f,
                        sweepAngle = (totalAllocation / 100f) * 260f,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Secondary progress (example - Tuition/Essentials)
                    val essentialsPercent = uiState.categories.filter { it.name.lowercase().contains("tuition") || it.name.lowercase().contains("rent") }.sumOf { it.percentage }
                    drawArc(
                        color = PrimaryBlue,
                        startAngle = 140f,
                        sweepAngle = (essentialsPercent / 100f) * 260f,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ALLOCATED",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalAllocation%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}
