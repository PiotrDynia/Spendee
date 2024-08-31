package com.example.spendee.core.presentation.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

@Composable
fun AnimatedVisibilityComposable(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var alpha by remember { mutableFloatStateOf(0f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 2000),
        label = "animated alpha"
    )

    var scale by remember { mutableFloatStateOf(0.6f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 2000),
        label = "animated scale"
    )

    LaunchedEffect(Unit) {
        while (true) {
            alpha = 1f
            scale = 1f
            delay(2000)
        }
    }

    Box(
        modifier = modifier
            .alpha(animatedAlpha)
            .scale(animatedScale)
    ) {
        content()
    }
}
