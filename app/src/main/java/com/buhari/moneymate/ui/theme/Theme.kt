package com.buhari.moneymate.ui.theme

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat

// =====================================================
// LIGHT COLOR SCHEME
// =====================================================

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = TextSecondary,
    tertiary = DeepPurple40,

    background = BackgroundLight,
    surface = CardWhite,

    onPrimary = Color.White,
    onSecondary = Color.White,

    onBackground = TextPrimary,
    onSurface = TextPrimary,

    primaryContainer = SecondaryPurpleContainer,
    onPrimaryContainer = PrimaryPurple,

    secondaryContainer = SecondaryPurpleContainer,
    onSecondaryContainer = PrimaryPurple,

    tertiaryContainer = TertiaryPurpleContainer,
    onTertiaryContainer = PrimaryPurple,

    surfaceVariant = Color(0xFFF2F2F2),
    surfaceContainer = NavbarLight,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = Color.White
)

// =====================================================
// DARK COLOR SCHEME
// =====================================================

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryPurple,
    onPrimary = Color.White,

    secondary = DarkSecondaryPurple,
    onSecondary = Color.Black,

    tertiary = Color(0xFFD9C4FF),

    background = DarkBackground,
    surface = DarkSurface,

    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,

    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFE9DDFF),

    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = Color(0xFFE1D5F5),

    tertiaryContainer = Color(0xFF433A56),
    onTertiaryContainer = DarkTextPrimary,

    surfaceVariant = DarkSurfaceVariant,
    surfaceContainer = NavbarDark,
    onSurfaceVariant = DarkTextSecondary,

    error = ErrorRedDark,
    onError = Color.Black
)

// =====================================================
// APP THEME
// =====================================================

@Composable
fun MoneyMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = DisplayMetrics.DENSITY_DEVICE_STABLE / 160f,
            fontScale = 1.0f
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}