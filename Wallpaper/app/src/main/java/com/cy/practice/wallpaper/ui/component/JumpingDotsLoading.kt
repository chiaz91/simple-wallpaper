package com.cy.practice.wallpaper.ui.component


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun JumpingDotsLoading(
    dotCount: Int = 4,
    dotRadius: Dp = 6.dp,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 16.dp,
    jumpHeight: Dp = 24.dp,
    jumpDuration: Int = 400,
    delayBetweenJumps: Int = 100,
    repeatMode: RepeatMode = RepeatMode.Restart
) {
    val dotRadiusPx = with(LocalDensity.current) { dotRadius.toPx() }
    val jumpHeightPx = with(LocalDensity.current) { jumpHeight.toPx() }
    val spacePx = with(LocalDensity.current) { spaceBetween.toPx() }
    val animatables = remember {
        List(dotCount) { Animatable(0f) }
    }

    LaunchedEffect(Unit) {
        // Start the jumping animation
        while (true) {
            // Sequential forward jumps
            animateSequentially(animatables, jumpHeightPx, jumpDuration, delayBetweenJumps)
            // Reverse jump logic
            if (repeatMode == RepeatMode.Reverse) {
                animateSequentially(
                    animatables,
                    jumpHeightPx,
                    jumpDuration,
                    delayBetweenJumps,
                    true
                )
            }
        }
    }

    // Calculate the total width of dots plus the space between & minimum height for the canvas
    val totalWidth = dotCount * (dotRadiusPx * 2) + (dotCount - 1) * spacePx
    val totalHeight = (dotRadiusPx + jumpHeightPx) * 2f

    Canvas(
        modifier = Modifier
            .wrapContentSize(align = Alignment.Center)
            .width(with(LocalDensity.current) { totalWidth.toDp() })
            .height(with(LocalDensity.current) { totalHeight.toDp() })
    ) {
        // Calculate the horizontal offset to center the dots
        val offsetX = (size.width - totalWidth) / 2

        // Calculate the vertical offset to center the dots within the canvas height
        val offsetY = (size.height - (dotRadiusPx * 2)) / 2

        for (i in 0 until dotCount) {
            val x = offsetX + dotRadiusPx + i * (dotRadiusPx * 2 + spacePx)
            val y = offsetY + dotRadiusPx + animatables[i].value
            drawCircle(color = dotColor, radius = dotRadiusPx, center = Offset(x, y))
        }
    }
}

private suspend fun animateSequentially(
    animatables: List<Animatable<Float, *>>,
    jumpHeightPx: Float,
    jumpDuration: Int,
    delayBetweenJumps: Int,
    reverse: Boolean = false,
) {
    coroutineScope {
        val orderedAnimatables = if (reverse) animatables.asReversed() else animatables
        val forwardJobs = orderedAnimatables.mapIndexed { index, anim ->
            launch {
                delay(index * delayBetweenJumps.toLong())
                anim.animateTo(targetValue = -jumpHeightPx, animationSpec = tween(jumpDuration / 2))
                anim.animateTo(targetValue = 0f, animationSpec = tween(jumpDuration / 2))
            }
        }
        forwardJobs.last().join()
    }
}

@Preview(showBackground = true)
@Composable
private fun JumpingDotsLoadingPreview(modifier: Modifier = Modifier) {
    val repeat = RepeatMode.Reverse

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JumpingDotsLoading()
            Spacer(Modifier.size(32.dp))
            JumpingDotsLoading(repeatMode = repeat)
        }
    }
}
