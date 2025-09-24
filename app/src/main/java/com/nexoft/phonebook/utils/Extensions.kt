package com.nexoft.phonebook.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

fun Modifier.bounceClick(onClick: () -> Unit): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "bounce"
    )

    this
        .scale(scale)
        .clickable {
            isPressed = true
            onClick()
        }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }

    this
}

fun Modifier.shadowWithColor(
    color: androidx.compose.ui.graphics.Color,
    alpha: Float = 0.4f,
    borderRadius: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(0f),
    shadowRadius: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(20f),
    offsetX: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(0f),
    offsetY: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(10f)
) = composed {
    this.graphicsLayer {
        this.shadowElevation = shadowRadius.toPx()
        this.shape = androidx.compose.ui.graphics.RectangleShape
        this.clip = false
        this.ambientShadowColor = color.copy(alpha = alpha)
        this.spotShadowColor = color.copy(alpha = alpha)
        this.translationX = offsetX.toPx()
        this.translationY = offsetY.toPx()
    }
}