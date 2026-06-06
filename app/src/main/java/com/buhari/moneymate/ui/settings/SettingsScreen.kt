package com.buhari.moneymate.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.buhari.moneymate.R
import com.buhari.moneymate.ui.components.ProfileCard
import com.buhari.moneymate.ui.components.SettingsItem
import com.buhari.moneymate.ui.components.SettingsSection
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            viewModel.exportDataToCsv(context, it) { success ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (success) "Data exported successfully" else "Failed to export data"
                    )
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importDataFromCsv(context, it) { success, added, duplicates ->
                scope.launch {
                    val message = when {
                        !success -> "Failed to import data"
                        added > 0 && duplicates > 0 -> "Imported $added items. $duplicates duplicates skipped."
                        added > 0 -> "Data imported successfully"
                        duplicates > 0 -> "Duplicates not allowed"
                        else -> "No valid data found in file"
                    }
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            item {
                ProfileCard(
                    userName = userProfile.name,
                    profileImage = userProfile.profileImage,
                    onEditClick = { showProfileSheet = true }
                )
            }

            item {
                SettingsSection(title = stringResource(R.string.appearance)) {
                    SettingsItem(
                        title = stringResource(R.string.theme),
                        subtitle = userProfile.theme,
                        icon = R.drawable.ic_theme,
                        onClick = { showThemeSheet = true }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.preferences)) {
                    SettingsItem(
                        title = stringResource(R.string.currency),
                        subtitle = stringResource(R.string.inr),
                        icon = R.drawable.ic_rupee,
                        onClick = { /* TODO: Open Currency Sheet */ }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string.language),
                        subtitle = stringResource(R.string.english),
                        icon = R.drawable.ic_language,
                        onClick = { /* TODO: Open Week Starts Sheet */ }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string.fingerprint),
                        subtitle = stringResource(R.string.biometric_authentication),
                        icon = R.drawable.ic_fingerprint,
                        trailing = {
                            var checked by remember { mutableStateOf(true) }
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    )
                }
            }

            // Section 3: Data Management
            item {
                SettingsSection(title = stringResource(R.string.data_management)) {
                    SettingsItem(
                        title = stringResource(R.string.export_data),
                        subtitle = stringResource(R.string.export_transactions_as_csv),
                        icon = R.drawable.ic_export,
                        onClick = { exportLauncher.launch("MoneyMate_Backup_${System.currentTimeMillis()}.csv") }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string.import_data),
                        subtitle = stringResource(R.string.restore_transactions_from_backup),
                        icon = R.drawable.ic_import,
                        onClick = { importLauncher.launch(arrayOf("text/comma-separated-values", "text/csv")) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string._clear_all_data),
                        subtitle = stringResource(R.string.permanently_remove_all_records),
                        icon = R.drawable.ic_clear_all,
                        isDestructive = true,
                        onClick = { showDeleteDialog = true }
                    )
                }
            }

            // Section 4: About
            item {
                SettingsSection(title = stringResource(R.string.about)) {
                    SettingsItem(
                        title = stringResource(R.string.app_version),
                        subtitle = stringResource(R.string.version_1_0_0),
                        icon = R.drawable.ic_info
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = "Help & Support",
                        subtitle = "FAQ",
                        icon = R.drawable.ic_help_and_support
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string.developer),
                        subtitle = stringResource(R.string.built_with_by_buhari),
                        icon = R.drawable.ic_developer
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string.privacy_policy),
                        subtitle = stringResource(R.string.data_is_stored_locally_on_your_device),
                        icon = R.drawable.ic_privacy_policy
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.clear_all_data),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = { 
                Text(
                    text = stringResource(R.string.this_action_will_permanently_delete_all_income_and_expense_records_and_cannot_be_undone),
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData {
                            showDeleteDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("All data cleared")
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            EditProfileContent(
                currentName = userProfile.name,
                currentImage = userProfile.profileImage,
                onSave = { name, image ->
                    viewModel.updateProfile(name, image)
                    showProfileSheet = false
                },
                onCancel = { showProfileSheet = false }
            )
        }
    }

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            ThemeSelectionContent(
                currentTheme = userProfile.theme,
                onThemeSelected = { theme ->
                    viewModel.updateTheme(theme)
                    showThemeSheet = false
                }
            )
        }
    }
}

@Composable
fun EditProfileContent(
    currentName: String,
    currentImage: String?,
    onSave: (String, String?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedImageUri by remember { mutableStateOf(currentImage) }
    var showOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it.toString()
        }
    }

    val tempUri = remember {
        val file = File(context.cacheDir, "temp_image.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempUri.toString()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.edit_profile),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image with Edit Action
        Box(
            modifier = Modifier
                .size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { showOptions = true },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                if (selectedImageUri.isNullOrEmpty()) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            // Edit Overlay Button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(32.dp)
                    .clickable { showOptions = true },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Image",
                    modifier = Modifier.padding(7.dp),
                    tint = Color.White
                )
            }
        }

        if (showOptions) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        showOptions = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                cameraLauncher.launch(tempUri)
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                        showOptions = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Profile Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, selectedImageUri) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun ThemeSelectionContent(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("Use device theme", "Light", "Dark")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 2.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Choose Theme",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        themes.forEach { theme ->
            Surface(
                onClick = { onThemeSelected(theme) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (currentTheme == theme) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = theme,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (currentTheme == theme) FontWeight.Bold else FontWeight.Normal
                    )
                    RadioButton(
                        selected = currentTheme == theme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MoneyMateTheme {
        SettingsScreen()
    }
}