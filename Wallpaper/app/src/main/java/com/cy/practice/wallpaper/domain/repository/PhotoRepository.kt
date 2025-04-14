package com.cy.practice.wallpaper.domain.repository

import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.ApiResult

interface PhotoRepository {
    suspend fun getPagedPhotos(query: String? = null): ApiResult<List<PixabayPhoto>, String>
}