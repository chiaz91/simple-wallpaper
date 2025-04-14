package com.cy.practice.wallpaper.ui.screen.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.getError
import com.cy.practice.wallpaper.shared.hasError
import com.cy.practice.wallpaper.ui.screen.gallery.component.EmptyPhotoScreen
import com.cy.practice.wallpaper.ui.screen.gallery.component.PagedPhotoGrid
import com.cy.practice.wallpaper.ui.screen.gallery.component.PhotoSearchBar
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    pagingPhotos: LazyPagingItems<PixabayPhoto>,
    onClickPhoto: (PixabayPhoto) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = pagingPhotos.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { pagingPhotos.refresh() },
        modifier = modifier.fillMaxSize(),
    ) {
        val scope = rememberCoroutineScope()
        val lazyGridState = rememberLazyStaggeredGridState()



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
                state = lazyGridState
            )
        }



        PhotoSearchBar(
            onSearch = {
                onSearch(it)
                scope.launch {
                    lazyGridState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        )
    }
}

