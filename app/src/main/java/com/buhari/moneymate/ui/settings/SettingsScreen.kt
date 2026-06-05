package com.buhari.moneymate.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buhari.moneymate.R
import com.buhari.moneymate.ui.components.ProfileCard
import com.buhari.moneymate.ui.components.SettingsItem
import com.buhari.moneymate.ui.components.SettingsSection
import com.buhari.moneymate.ui.theme.MoneyMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
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
                ProfileCard()
            }

            item {
                SettingsSection(title = stringResource(R.string.appearance)) {
                    SettingsItem(
                        title = stringResource(R.string.theme),
                        subtitle = stringResource(R.string.dark),
                        icon = R.drawable.ic_theme,
                        onClick = { /* TODO: Open Theme Bottom Sheet */ }
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
                        subtitle = stringResource(R.string.export_transactions_as_json_or_csv),
                        icon = R.drawable.ic_export,
                        onClick = { /* TODO: Export */ }
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
                        onClick = { /* TODO: Import */ }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    SettingsItem(
                        title = stringResource(R.string.clear_all_data),
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
                        title = stringResource(R.string.privacy),
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
            title = { Text("Clear All Data?") },
            text = { Text("This action will permanently delete all income and expense records and cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MoneyMateTheme {
        SettingsScreen()
    }
}