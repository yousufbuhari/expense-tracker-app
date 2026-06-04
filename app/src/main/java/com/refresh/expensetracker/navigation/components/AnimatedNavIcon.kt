package com.refresh.expensetracker.navigation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.refresh.expensetracker.navigation.AppDestinations
import kotlinx.coroutines.launch

@Composable
fun AnimatedNavIcon(
    destination: AppDestinations,
    isSelected: Boolean
) {
    val translationY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(if (isSelected) 1f else 0.5f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            launch {
                // Instant snap to squish, then spring out
                translationY.snapTo(3f)
                translationY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.35f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                scale.snapTo(0.85f)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.35f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(100)
                )
            }
        } else {
            launch { translationY.animateTo(0f, tween(100)) }
            launch { scale.animateTo(1f, tween(100)) }
            launch { alpha.animateTo(0.5f, tween(100)) }
        }
    }

    Icon(
        painter = painterResource(destination.icon),
        contentDescription = destination.label,
        modifier = Modifier
            .size(22.dp)
            .graphicsLayer {
                this.translationY = translationY.value
                this.scaleX = scale.value
                this.scaleY = scale.value
                this.alpha = alpha.value
                this.transformOrigin = TransformOrigin(0.5f, 1.0f)
            }
    )
}