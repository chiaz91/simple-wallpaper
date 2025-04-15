package com.cy.practice.wallpaper.data.remote

import com.cy.practice.wallpaper.BuildConfig
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhotosResponse
import com.cy.practice.wallpaper.shared.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.net.URLEncoder

// API doc: https://pixabay.com/api/docs/
class PixabayApiImpl(
    private val client: HttpClient,
) : PixabayApi {
    private val BASE_URL = "https://pixabay.com/api/"
    private val KEY = BuildConfig.PixabayApiKey


    override suspend fun getPhotos(
        page: Int,
        pageSize: Int,
        query: String?,
    ): ApiResult<PixabayPhotosResponse, String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.get(BASE_URL) {
                    parameter("key", KEY)
                    parameter("image_type", "photo")
                    if (!query.isNullOrBlank()) {
                        parameter("q", URLEncoder.encode(query, Charsets.UTF_8.name()))
                    }
                    parameter("per_page", pageSize)
                    parameter("page", page)
                }

                if (response.status == HttpStatusCode.OK) {
                    val result = response.body<PixabayPhotosResponse>()
                    ApiResult.Success(result)
                } else {
                    ApiResult.Error("Error: ${response.status.description}")
                }
            } catch (e: Exception) {
                ensureActive()
                ApiResult.Error("Exception: ${e.message}")
            }
        }
    }
}