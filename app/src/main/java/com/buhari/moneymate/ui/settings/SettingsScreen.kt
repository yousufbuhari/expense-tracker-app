package com.buhari.moneymate.ui.settings

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.R
import com.buhari.moneymate.ui.components.EditProfileContent
import com.buhari.moneymate.ui.components.LanguageSelectionContent
import com.buhari.moneymate.ui.components.ProfileCard
import com.buhari.moneymate.ui.components.SettingsItem
import com.buhari.moneymate.ui.components.SettingsSection
import com.buhari.moneymate.ui.components.ThemeSelectionContent
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@SuppressLint("LocalContextResourcesRead")
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
    var showLanguageSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val res = context.resources
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            viewModel.exportDataToCsv(context, it) { success ->
                val message = if (success) res.getString(R.string.export_success) else res.getString(R.string.export_failed)
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importDataFromCsv(context, it) { success, added, duplicates ->
                val message = when {
                    !success -> res.getString(R.string.import_failed)
                    added > 0 && duplicates > 0 -> res.getString(R.string.import_success_with_duplicates, added, duplicates)
                    added > 0 -> res.getString(R.string.import_success)
                    duplicates > 0 -> res.getString(R.string.duplicates_not_allowed)
                    else -> res.getString(R.string.import_no_data)
                }
                scope.launch {
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                    val themeSubtitle = when (userProfile.theme) {
                        "Dark" -> stringResource(R.string.dark)
                        "Light" -> stringResource(R.string.light)
                        else -> stringResource(R.string.use_device_theme)
                    }
                    SettingsItem(
                        title = stringResource(R.string.theme),
                        subtitle = themeSubtitle,
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
                        subtitle = if (userProfile.language == "ta") stringResource(R.string.tamil) else stringResource(R.string.english),
                        icon = R.drawable.ic_language,
                        onClick = { showLanguageSheet = true }
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
                        onClick = { 
                            val fileName = "MoneyMate_Backup_${System.currentTimeMillis()}.csv"
                            exportLauncher.launch(fileName) 
                        }
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
                        title = stringResource(R.string.help_support),
                        subtitle = stringResource(R.string.faq),
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
                            val message = res.getString(R.string.all_data_cleared)
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
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

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            LanguageSelectionContent(
                currentLanguage = userProfile.language,
                onLanguageSelected = { languageCode ->
                    viewModel.updateLanguage(languageCode)
                    showLanguageSheet = false
                }
            )
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