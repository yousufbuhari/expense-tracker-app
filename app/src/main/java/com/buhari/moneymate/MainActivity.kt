package com.buhari.moneymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.navigation.MoneyMateApp
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val userProfile by settingsViewModel.userProfile.collectAsState()
            
            val darkTheme = when (userProfile.theme) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            MoneyMateTheme(darkTheme = darkTheme) {
                MoneyMateApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavigationPreview() {
    MoneyMateTheme {
        MoneyMateApp()
    }
}