package com.cy.practice.wallpaper.ui.screen.gallery.component

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.debounceClick
import com.cy.practice.wallpaper.shared.pagingLoadStateItem
import com.cy.practice.wallpaper.shared.shimmerEffect
import com.cy.practice.wallpaper.ui.component.JumpingDotsLoading


@Composable
fun PagedPhotoGrid(
    pagingPhotos: LazyPagingItems<PixabayPhoto>,
    onCLick: (PixabayPhoto) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = WindowInsets.statusBars.asPaddingValues()
) {

    val cellConfiguration = if (LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT) {
        StaggeredGridCells.Fixed(2)
    } else StaggeredGridCells.Adaptive(250.dp)

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        state = state,
        columns = cellConfiguration,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pagingLoadStateItem(
            pagingPhotos.loadState.prepend,
            keySuffix = "prepend",
            loading = { Loading() },
            error = { ErrorMessage(it, { pagingPhotos.retry() }) },
        )

        items(pagingPhotos.itemCount) { index ->
            val photo = pagingPhotos[index]
            if (photo != null) {
                PhotoCard(
                    photo,
                    Modifier.debounceClick { onCLick(photo) }
                )
            } else {
                Placeholder()
            }
        }

        pagingLoadStateItem(
            pagingPhotos.loadState.append,
            keySuffix = "append",
            loading = { Loading() },
            error = { ErrorMessage(it, { pagingPhotos.retry() }) },
        )
    }
}

@Composable
private fun Placeholder(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .then(modifier),

        ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect()
        )
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        JumpingDotsLoading()
    }
}


@Composable
private fun ErrorMessage(
    errorState: LoadState.Error,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errorMessage = errorState.error.message ?: "Unknown error occurred"
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(errorMessage)
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
