package com.cy.practice.wallpaper.ui.screen.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.getError
import com.cy.practice.wallpaper.shared.hasError
import com.cy.practice.wallpaper.ui.screen.gallery.component.EmptyPhotoScreen
import com.cy.practice.wallpaper.ui.screen.gallery.component.PagedPhotoGrid


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    pagingPhotos: LazyPagingItems<PixabayPhoto>,
    onClickPhoto: (PixabayPhoto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = pagingPhotos.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { pagingPhotos.refresh() },
        modifier = modifier.fillMaxSize()
    ) {

        if (!isLoading && pagingPhotos.itemCount == 0) {
            val errorMessage = if (pagingPhotos.loadState.hasError()) {
                pagingPhotos.loadState.getError()?.message ?: "unknown error"
            } else null
            EmptyPhotoScreen(errorMessage = errorMessage)
        } else {
            PagedPhotoGrid(
                pagingPhotos,
                onClickPhoto,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

