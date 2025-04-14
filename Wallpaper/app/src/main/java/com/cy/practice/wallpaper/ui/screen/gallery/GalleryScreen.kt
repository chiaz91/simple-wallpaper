package com.cy.practice.wallpaper.ui.screen.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto


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
            val error = (pagingPhotos.loadState.refresh as? LoadState.Error)
            val errorMessage = error?.error?.message
            EmptyPhotoScreen(errorMessage = errorMessage)
        } else {
            PhotoGrid(
                pagingPhotos,
                onClickPhoto,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun PhotoGrid(
    pagingPhotos: LazyPagingItems<PixabayPhoto>,
    onClickPhoto: (PixabayPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(pagingPhotos.itemCount) { index ->
            val photo = pagingPhotos[index]
            photo?.let {
                PhotoCard(photo, modifier = Modifier.clickable { onClickPhoto(photo) })
            }
        }

        if (pagingPhotos.loadState.append == LoadState.Loading) {
            item(key = "append_loading", span = { GridItemSpan(2) }) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

