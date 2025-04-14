package com.cy.practice.wallpaper.ui.screen.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
    vm: GalleryViewModel = hiltViewModel()
) {

    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.loadData() }

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { vm.loadData() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(photos) {
                Text("Photo(id=${it.photoId},\nurl=${it.previewUrl})")
                HorizontalDivider()
            }
        }
    }

}

