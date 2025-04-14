package com.cy.practice.wallpaper.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun PulseLoading(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    val transition = rememberInfiniteTransition(label = "")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.4f at 300
                0.4f at 450
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val strokeColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier.size(size)) {
        val radius = size.toPx() / 2 * pulse
        val alpha = 1f - pulse

        drawCircle(
            color = strokeColor.copy(alpha = alpha),
            radius = radius,
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PulseLoadingPreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            PulseLoading()
        }

    }
}