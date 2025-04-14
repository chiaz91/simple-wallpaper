package com.cy.practice.wallpaper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.cy.practice.wallpaper.ui.screen.gallery.GalleryScreen
import com.cy.practice.wallpaper.ui.screen.gallery.GalleryViewModel
import com.cy.practice.wallpaper.ui.screen.photo_detail.PhotoDetailScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val galleryViewModel: GalleryViewModel = hiltViewModel()
    val pagingPhotos = galleryViewModel.pagingPhotos.collectAsLazyPagingItems()


    NavHost(
        navController = navController,
        startDestination = Routes.Gallery
    ) {
        composable<Routes.Gallery> {
            GalleryScreen(
                pagingPhotos,
                onClickPhoto = { photo ->
                    navController.navigate(Routes.PhotoDetail(photo))
                },
                onSearch = {galleryViewModel.query(it)},
                modifier = modifier
            )
        }

        composable<Routes.PhotoDetail>(
            typeMap = Routes.PhotoDetail.typeMap
        ) { backStackEntry ->
            val route: Routes.PhotoDetail = backStackEntry.toRoute()
            PhotoDetailScreen(
                route.photo,
                onDismiss = { navController.navigateUp() }
            )
        }
    }
}