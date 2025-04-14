package com.cy.practice.wallpaper.ui.screen.photo_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.ui.screen.photo_detail.component.ZoomableImage


@Composable
fun PhotoDetailScreen(
    photo: PixabayPhoto,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        ZoomableImage(photo)

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            Icon(Icons.Default.Close, "dismiss button")
        }

    }
}







