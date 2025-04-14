package com.cy.practice.wallpaper.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cy.practice.wallpaper.data.remote.PixabayApi
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.shared.ApiResult
import com.cy.practice.wallpaper.shared.Constants.NETWORK_PAGE_SIZE

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
                LoadResult.Page(
                    itemsBefore = (page - 1) * NETWORK_PAGE_SIZE,
                    data = response.data,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    // when LoadParams is LoadParams.Refresh,  loadSize = initialLoadSize (3 times of page size by default)
                    // hence, need to calculate proper nextKey to prevent duplicate data loaded
                    nextKey = if (response.data.isEmpty()) null else page + loadSize / NETWORK_PAGE_SIZE
                )
            } else {
                val errorMessage = (response as? ApiResult.Error)?.error ?: "no response"
                LoadResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}