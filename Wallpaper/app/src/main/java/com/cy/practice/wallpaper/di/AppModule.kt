package com.cy.practice.wallpaper.di

import android.content.Context
import com.cy.practice.wallpaper.data.remote.PixabayApi
import com.cy.practice.wallpaper.data.remote.PixabayApiImpl
import com.cy.practice.wallpaper.data.repository.AndroidDownloader
import com.cy.practice.wallpaper.data.repository.PhotoRepositoryImpl
import com.cy.practice.wallpaper.domain.repository.Downloader
import com.cy.practice.wallpaper.domain.repository.PhotoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun providesHttpClient(): HttpClient {
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        val timberLogger = object : Logger {
            override fun log(message: String) {
                Timber.tag("ktor.Http").d(message)
            }
        }
        return HttpClient(Android) {
            install(Logging) {
                level = LogLevel.INFO
                logger = timberLogger // Logger.ANDROID
            }
            install(ContentNegotiation) {
                json(json = json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 3000
                connectTimeoutMillis = 3000
                socketTimeoutMillis = 3000
            }
        }
    }

    @Provides
    @Singleton
    fun providesPixabayApi(
        httpClient: HttpClient,

        ): PixabayApi {
        return PixabayApiImpl(httpClient)
    }

    @Provides
    @Singleton
    fun providesPhotosRepository(
        pixabayApi: PixabayApi
    ): PhotoRepository {
        return PhotoRepositoryImpl(pixabayApi)
    }

    @Provides
    @Singleton
    fun providesAndroidDownloader(
        @ApplicationContext context: Context,
    ): Downloader {
        return AndroidDownloader(context)
    }
}