package com.cy.practice.wallpaper.ui.screen.photo_detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.gestureZoomable
import com.cy.practice.wallpaper.ui.component.JumpingDotsLoading


@Composable
fun ZoomableImage(
    photo: PixabayPhoto,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.largeImageUrl)
            .crossfade(false)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .gestureZoomable()
            .aspectRatio(photo.calAspectRation())
    )

    if (painter.state is AsyncImagePainter.State.Loading) {
        JumpingDotsLoading()
    }
}

