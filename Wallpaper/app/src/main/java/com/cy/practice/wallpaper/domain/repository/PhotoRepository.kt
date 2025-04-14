package com.cy.practice.wallpaper.domain.repository

import androidx.paging.PagingData
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPagedPhotos(query: String? = null): Flow<PagingData<PixabayPhoto>>
}