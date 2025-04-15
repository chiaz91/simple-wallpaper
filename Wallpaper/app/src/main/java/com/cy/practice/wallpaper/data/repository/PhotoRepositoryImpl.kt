package com.cy.practice.wallpaper.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cy.practice.wallpaper.data.paging.PixabayPhotosPagingSource
import com.cy.practice.wallpaper.data.remote.PixabayApi
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.domain.repository.PhotoRepository
import com.cy.practice.wallpaper.shared.Constants.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val service: PixabayApi
) : PhotoRepository {

    override fun getPagedPhotos(query: String?): Flow<PagingData<PixabayPhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { PixabayPhotosPagingSource(query, service) }
        ).flow
    }
}