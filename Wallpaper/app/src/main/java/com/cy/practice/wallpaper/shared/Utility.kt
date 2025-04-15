package com.cy.practice.wallpaper.shared


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState


fun Context.viewImage(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun getMimeFromFileName(fileName: String?): String? {
    val map = MimeTypeMap.getSingleton()
    val ext = MimeTypeMap.getFileExtensionFromUrl(fileName)
    return map.getMimeTypeFromExtension(ext)
}

fun generateFileNameFromUrl(url: String, fallbackExtension: String = "jpg"): String {
    // Try to extract filename from path
    val lastSegment = url.substring(url.lastIndexOf('/') + 1) // Uri.parse(url).lastPathSegment
    return if (lastSegment.isNotBlank() && lastSegment.contains('.')) {
        lastSegment
    } else {
        // Fallback: timestamp-based name
        "download_${System.currentTimeMillis()}.$fallbackExtension"
    }
}


// compose util
@Composable
fun onDebounceClick(
    debounceTimeMillis: Long = 1000L,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTimeMillis: Long by remember { mutableLongStateOf(0L) }
    return {
        System.currentTimeMillis().let { currentTimeMillis ->
            if ((currentTimeMillis - lastClickTimeMillis) >= debounceTimeMillis) {
                lastClickTimeMillis = currentTimeMillis
                onClick()
            }
        }
    }
}

@Composable
fun rememberVisibilityBasedOnScroll(lazyGridState: LazyStaggeredGridState): State<Boolean> {
    var previousFirstIndex by remember { mutableIntStateOf(0) }
    return remember {
        derivedStateOf {
            val currentFirstIndex = lazyGridState.firstVisibleItemIndex
            val isScrollingUp = currentFirstIndex < previousFirstIndex
            previousFirstIndex = currentFirstIndex
            isScrollingUp || lazyGridState.firstVisibleItemIndex == 0
        }
    }
}


fun LazyStaggeredGridScope.pagingLoadStateItem(
    loadState: LoadState,
    keySuffix: String? = null,
    loadingItemSpan: StaggeredGridItemSpan = StaggeredGridItemSpan.FullLine,
    loading: (@Composable LazyStaggeredGridItemScope.() -> Unit)? = null,
    errorItemSpan: StaggeredGridItemSpan = StaggeredGridItemSpan.FullLine,
    error: (@Composable LazyStaggeredGridItemScope.(LoadState.Error) -> Unit)? = null,
) {
    if (loading != null && loadState == LoadState.Loading) {
        item(
            key = keySuffix?.let { "loadingItem_$it" },
            content = loading,
            span = loadingItemSpan,
        )
    }
    if (error != null && loadState is LoadState.Error) {
        item(
            key = keySuffix?.let { "errorItem_$it" },
            content = { error(loadState) },
            span = errorItemSpan,
        )
    }
}

fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

fun Modifier.debounceClick(
    debounceTimeMillis: Long = 500L,
    onClick: () -> Unit
): Modifier = composed {
    val debounce = onDebounceClick(debounceTimeMillis, onClick)
    clickable { debounce() }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = ""
    )

    onGloballyPositioned {
        size = it.size
    }.background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
}


@Composable
fun Modifier.gestureZoomable(): Modifier {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }

    return this
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { tapOffset ->
                    zoom = if (zoom > 1f) 1f else 2f
                    offset = calculateDoubleTapOffset(zoom, size, tapOffset)
                }
            )
        }
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { centroid, pan, gestureZoom, _ ->
                    offset = offset.calculateNewOffset(
                        centroid, pan, zoom, gestureZoom, size
                    )
                    zoom = maxOf(1f, zoom * gestureZoom)
                }
            )
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom;
            scaleY = zoom
            transformOrigin = TransformOrigin(0f, 0f)
        }
}


fun Offset.calculateNewOffset(
    centroid: Offset,
    pan: Offset,
    zoom: Float,
    gestureZoom: Float,
    size: IntSize
): Offset {
    val newScale = maxOf(1f, zoom * gestureZoom)
    val newOffset = (this + centroid / zoom) -
            (centroid / newScale + pan / zoom)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}

fun calculateDoubleTapOffset(
    zoom: Float,
    size: IntSize,
    tapOffset: Offset
): Offset {
    val newOffset = Offset(tapOffset.x, tapOffset.y)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}


// paging helper
fun CombinedLoadStates.isLoading(): Boolean {
    return refresh is LoadState.Loading ||
            append is LoadState.Loading ||
            prepend is LoadState.Loading
}

fun CombinedLoadStates.hasError(): Boolean {
    return refresh is LoadState.Error
            || append is LoadState.Error
            || prepend is LoadState.Error
}


fun CombinedLoadStates.getError(): Throwable? {
    return when {
        refresh is LoadState.Error -> (refresh as LoadState.Error).error
        prepend is LoadState.Error -> (prepend as LoadState.Error).error
        append is LoadState.Error -> (append as LoadState.Error).error
        else -> null
    }
}