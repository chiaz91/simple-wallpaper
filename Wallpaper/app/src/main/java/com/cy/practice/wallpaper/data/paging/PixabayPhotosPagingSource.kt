package com.cy.practice.wallpaper.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cy.practice.wallpaper.data.remote.PixabayApi
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.ApiResult
import com.cy.practice.wallpaper.shared.Constants.NETWORK_PAGE_SIZE
import timber.log.Timber

private const val STARTING_PAGE_INDEX = 1

class PixabayPhotosPagingSource(
    private val query: String? = null,
    private val service: PixabayApi
) : PagingSource<Int, PixabayPhoto>() {

    // provide a key for load() method, when invalidate or refresh is called
    override fun getRefreshKey(state: PagingState<Int, PixabayPhoto>): Int? {
        // state contains list of cached pages, attempt to find nearest page from anchorPosition
        // however, page does not contain actual page number(or key), so trying to retrieve the key
        // from page's prev and next key
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PixabayPhoto> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val loadSize = params.loadSize

        return try {
            val response = service.getPhotos(page, loadSize, query)
            if (response is ApiResult.Success) {
                // response.totalHits indicates the total number of results matching the query. We
                // can use it to calculate the total number of pages or items before or after the
                // current page.
                val photos = response.data.hits
                val itemBefore = (page - 1) * NETWORK_PAGE_SIZE
                val itemAfter = response.data.totalHits - itemBefore - photos.size

                LoadResult.Page(
                    itemsBefore = (page - 1) * NETWORK_PAGE_SIZE,
                    itemsAfter = itemAfter,
                    data = photos,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (itemAfter == 0 || photos.isEmpty()) {
                        null
                    } else {
                        // For LoadParams.Refresh, loadSize = initialLoadSize (3x page size).
                        // Calculate nextKey carefully to avoid duplicate loads.
                        page + loadSize / NETWORK_PAGE_SIZE
                    }
                ).also {
                    Timber.d("Result.Page::Curr(key=$page, items=${photos.size}), Prev(key=${it.prevKey}, itemsBefore=${it.itemsBefore}), Next(key=${it.nextKey}, itemsAfter=${it.itemsAfter})")
                }
            } else {
                val errorMessage = (response as? ApiResult.Error)?.error ?: "no response"
                LoadResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}