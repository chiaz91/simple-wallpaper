package com.cy.practice.wallpaper

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WallpaperApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    // Coil: configuring the singleton ImageLoader
    override fun newImageLoader(): ImageLoader {
        return ImageLoader(this).newBuilder()
//            .memoryCachePolicy(CachePolicy.ENABLED)
//            .memoryCache {
//                MemoryCache.Builder(this)
//                    .maxSizePercent(0.2)
//                    .strongReferencesEnabled(true)
//                    .build()
//            }
//            .diskCachePolicy(CachePolicy.ENABLED)
//            .diskCache {
//                DiskCache.Builder()
//                    .maxSizePercent(0.03)
//                    .directory(cacheDir)
//                    .build()
//            }
            .logger(DebugLogger(Log.INFO))
            .build()
    }

}