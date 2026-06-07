package com.buhari.moneymate.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buhari.moneymate.ui.viewmodel.SettingsViewModel
import com.buhari.moneymate.data.entity.UserProfile
import com.buhari.moneymate.ui.theme.Manrope
import com.buhari.moneymate.ui.theme.PrimaryPurple
import com.buhari.moneymate.utils.BiometricHelper
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val userProfileState by settingsViewModel.userProfile.collectAsState()

    SplashScreenContent(
        userProfile = userProfileState,
        onSplashFinished = onSplashFinished
    )
}

@Composable
fun SplashScreenContent(
    userProfile: UserProfile?,
    onSplashFinished: () -> Unit
) {
    val currentProfileState by rememberUpdatedState(userProfile)
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var startAnimation by remember { mutableStateOf(false) }
    
    // "Money" animation
    val alpha1 = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 100),
        label = "Alpha1"
    )
    val offsetY1 = animateFloatAsState(
        targetValue = if (startAnimation) 0f else 20f,
        animationSpec = tween(1000, delayMillis = 100, easing = EaseOutQuart),
        label = "Offset1"
    )

    // "Mate" animation (staggered delay)
    val alpha2 = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 300),
        label = "Alpha2"
    )
    val offsetY2 = animateFloatAsState(
        targetValue = if (startAnimation) 0f else 20f,
        animationSpec = tween(1000, delayMillis = 300, easing = EaseOutQuart),
        label = "Offset2"
    )

    // Subtle scale-down for the whole container (the "Land" effect)
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 1.05f,
        animationSpec = tween(1200, easing = EaseOutQuart),
        label = "Scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotPulse"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1800.milliseconds) // Slightly longer to appreciate the animation

        while (currentProfileState == null) {
            delay(100.milliseconds)
        }

        val profile = currentProfileState!!
        if (profile.isBiometricEnabled && activity != null && BiometricHelper.isBiometricAvailable(activity)) {
            BiometricHelper.showBiometricPrompt(
                activity = activity,
                onSuccess = { onSplashFinished() },
                onError = { _, _ -> activity.finish() },
                onFailed = {}
            )
        } else {
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.graphicsLayer(
                scaleX = scaleAnim.value,
                scaleY = scaleAnim.value
            )
        ) {
            Text(
                text = "Money",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Manrope,
                lineHeight = 60.sp,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = alpha1.value,
                        translationY = offsetY1.value
                    )
            )
            Text(
                text = "Mate",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Manrope,
                lineHeight = 60.sp,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = alpha2.value,
                        translationY = offsetY2.value
                    )
            )
            Text(
                text = "․",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Manrope,
                modifier = Modifier
                    .alpha(if (startAnimation) dotAlpha else 0f),
                lineHeight = 60.sp
            )
        }
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    SplashScreenContent(
        userProfile = UserProfile(),
        onSplashFinished = {}
    )
}