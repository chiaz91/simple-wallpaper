package com.cy.practice.wallpaper.ui.screen.gallery

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
    vm: GalleryViewModel = hiltViewModel()
) {
    val pagingPhotos = vm.pagingPhotos.collectAsLazyPagingItems()
    val isLoading = pagingPhotos.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { vm.query() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(pagingPhotos.itemCount) { index ->
                val photo = pagingPhotos[index]
                photo?.let {
                    PhotoCard(photo)
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

}

