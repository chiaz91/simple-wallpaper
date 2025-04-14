package com.cy.practice.wallpaper.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixabayPhotosResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<PixabayPhoto>
)


@Serializable
data class PixabayPhoto(
    @SerialName("id") val photoId: Int,
    @SerialName("pageURL") val pageUrl: String,
    val tags: String,
    @SerialName("webformatURL") val previewUrl: String,
    @SerialName("largeImageURL") val largeImageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
) {
    fun calAspectRation() = imageWidth.toFloat() / imageHeight.toFloat()
}

