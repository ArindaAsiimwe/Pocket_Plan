package com.example.pocketplan.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pocketplan.ui.auth.AuthViewModel
import com.example.pocketplan.ui.components.PocketPlanTopBar
import com.example.pocketplan.ui.theme.*
import com.example.pocketplan.utils.ImageStorageHelper
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val imageStorageHelper = remember { ImageStorageHelper(context) }
    
    var showImageOptions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.user) {
        state.user?.let { 
            if (name.isEmpty()) name = it.name 
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            authViewModel.clearError()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = imageStorageHelper.saveImageToInternalStorage(it)
            if (path != null) {
                authViewModel.updateProfilePicture(path)
            }
        }
        showImageOptions = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val path = imageStorageHelper.saveBitmapToInternalStorage(it)
            if (path != null) {
                authViewModel.updateProfilePicture(path)
            }
        }
        showImageOptions = false
    }

    Scaffold(
        topBar = {
            PocketPlanTopBar(
                title = "Profile",
                onNotificationClick = { },
                onLogoutClick = onLogoutClick
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(4.dp, PrimaryBlue, CircleShape)
                    .clickable { showImageOptions = true }
            ) {
                if (state.user?.profilePicPath != null) {
                    AsyncImage(
                        model = File(state.user!!.profilePicPath!!),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = PrimaryBlue.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        color = PrimaryBlue,
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Name Display
            Text(
                text = state.user?.name ?: "User",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Edit Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextSecondary
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { authViewModel.updateName(name) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.8f)
                        ),
                        enabled = name.isNotBlank() && name != state.user?.name && !state.isLoading
                    ) {
                        Text("Update Name", fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("New Password", color = TextSecondary) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextSecondary
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { 
                            authViewModel.updatePassword(password)
                            password = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.8f)
                        ),
                        enabled = password.length >= 6 && !state.isLoading
                    ) {
                        Text("Change Password", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showImageOptions) {
            ModalBottomSheet(
                onDismissRequest = { showImageOptions = false },
                sheetState = sheetState,
                containerColor = SurfaceWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 16.dp)
                ) {
                    Text(
                        text = "Profile Photo",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        ImageSourceOption(
                            icon = Icons.Default.PhotoCamera,
                            label = "Camera",
                            onClick = { cameraLauncher.launch(null) }
                        )
                        ImageSourceOption(
                            icon = Icons.Default.PhotoLibrary,
                            label = "Gallery",
                            onClick = { galleryLauncher.launch("image/*") }
                        )
                    }
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}

@Composable
fun ImageSourceOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = BackgroundLight,
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}
