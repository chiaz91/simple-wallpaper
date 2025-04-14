package com.cy.practice.wallpaper.data.remote

import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.ApiResult

interface PixabayApi {
    suspend fun getPhotos(
        page: Int = 1,
        pageSize: Int = 20,
        query: String? = null,
    ): ApiResult<List<PixabayPhoto>, String>

}