package com.cy.practice.wallpaper.data.repository

import com.cy.practice.wallpaper.data.remote.PixabayApi
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.domain.repository.PhotoRepository
import com.cy.practice.wallpaper.shared.ApiResult
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val service: PixabayApi
) : PhotoRepository {

    override suspend fun getPagedPhotos(query: String?): ApiResult<List<PixabayPhoto>, String> {
        return service.getPhotos(query = query)
    }
}