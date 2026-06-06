package com.buhari.moneymate

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.navigation.MoneyMateApp
import com.buhari.moneymate.ui.theme.MoneyMateTheme
import com.buhari.moneymate.ui.viewmodel.SettingsViewModel
import com.buhari.moneymate.utils.BiometricHelper
import java.util.Locale

class MainActivity : FragmentActivity() {
    @SuppressLint("LocalContextConfigurationRead")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val userProfileState by settingsViewModel.userProfile.collectAsState()
            
            val userProfile = userProfileState ?: return@setContent // Don't render until profile is loaded
            
            val context = LocalContext.current
            val language = userProfile.language

            // Update locale configuration
            val locale = Locale.forLanguageTag(language)
            Locale.setDefault(locale)
            val resources = context.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            val darkTheme = when (userProfile.theme) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            val activity = context as? FragmentActivity
            var isAuthenticated by remember { mutableStateOf(false) }
            var authInitiated by remember { mutableStateOf(false) }

            // Handle biometric authentication
            LaunchedEffect(userProfile.isBiometricEnabled) {
                if (userProfile.isBiometricEnabled && !isAuthenticated && !authInitiated) {
                    authInitiated = true
                    if (activity != null && BiometricHelper.isBiometricAvailable(activity)) {
                        BiometricHelper.showBiometricPrompt(
                            activity = activity,
                            onSuccess = { 
                                isAuthenticated = true
                                authInitiated = false 
                            },
                            onError = { _, _ -> 
                                authInitiated = false
                                // Close app if authentication is required but failed/canceled
                                activity.finish()
                            },
                            onFailed = { 
                                authInitiated = false 
                            }
                        )
                    } else {
                        isAuthenticated = true
                    }
                }
            }

            MoneyMateTheme(
                darkTheme = darkTheme,
                language = language,
                currency = userProfile.currency
            ) {
                MoneyMateApp(language = language)
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
