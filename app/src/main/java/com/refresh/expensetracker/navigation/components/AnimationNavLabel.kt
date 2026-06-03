package com.refresh.expensetracker.navigation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.refresh.expensetracker.navigation.AppDestinations
import com.refresh.expensetracker.ui.theme.GoogleSans
import kotlinx.coroutines.launch

@Composable
fun AnimatedNavLabel(
    destination: AppDestinations,
    isSelected: Boolean
) {
    val alpha = remember { Animatable(if (isSelected) 1f else 0.55f) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(100)
                )
            }
            launch {
                scale.snapTo(0.95f)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.4f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        } else {
            launch { alpha.animateTo(0.55f, tween(100)) }
            launch { scale.animateTo(1f, tween(100)) }
        }
    }

    Text(
        text = destination.label,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = GoogleSans,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        ),
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            this.scaleX = scale.value
            this.scaleY = scale.value
        }
    )
}