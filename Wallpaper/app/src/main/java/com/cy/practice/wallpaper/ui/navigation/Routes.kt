package com.cy.practice.wallpaper.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.toRoute
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

sealed interface Routes {
    @Serializable
    data object Gallery : Routes

    @Serializable
    data class PhotoDetail(
        // simplify for practice, do not recommended to pass complex object in navigation
        val photo: PixabayPhoto
    ) : Routes {
        companion object {
            // serialize the custom object as json
            val typeMap = mapOf(typeOf<PixabayPhoto>() to serializableType<PixabayPhoto>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<PixabayPhoto>(typeMap)
        }
    }
}

// helper method to create NavType for custom object used in navigation
inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }
}
