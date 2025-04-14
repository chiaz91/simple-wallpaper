package com.cy.practice.wallpaper.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.cy.practice.wallpaper.domain.model.DownloadState
import com.cy.practice.wallpaper.domain.repository.Downloader
import com.cy.practice.wallpaper.shared.generateFileNameFromUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow


class AndroidDownloader(
    context: Context,
) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)


    override fun download(url: String, fileName: String?): Long {
        val downloadFileName = fileName ?: generateFileNameFromUrl(url)
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Downloading")
            setMimeType("image/*")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        // can register BroadcastReceiver to listen for android.intent.action.DOWNLOAD_COMPLETE
        return downloadManager.enqueue(request)
    }

    override fun observeDownload(
        jobId: Long,
        pollIntervalMs: Long,
    ): Flow<DownloadState> {
        return flow {
            var keepPolling = true

            while (keepPolling) {
                val state = downloadManager.queryDownloadStatus(jobId)
                emit(state)

                if (state is DownloadState.Success || state is DownloadState.Failed) {
                    keepPolling = false
                } else {
                    delay(pollIntervalMs)
                }
            }
        }
            .distinctUntilChanged()
    }


    private fun DownloadManager.queryDownloadStatus(
        downloadId: Long
    ): DownloadState {
        val query = DownloadManager.Query().setFilterById(downloadId)
        this.query(query)?.use {
            val statusIndex = it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
            val totalBytesIndex = it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val downloadedBytesIndex =
                it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

            if (it.moveToFirst()) {
                val status = it.getInt(statusIndex)
                val totalBytes = it.getLong(totalBytesIndex)
                val downloadedBytes = it.getLong(downloadedBytesIndex)

                return when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        val uri = getUriForDownloadedFile(downloadId)
                        DownloadState.Success(uri)
                    }

                    DownloadManager.STATUS_FAILED -> {
                        val reason = it.getInt(
                            it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                        )
                        DownloadState.Failed("Download failed with reason code: $reason")
                    }

                    else -> {
                        val process = if (totalBytes > 0) {
                            (downloadedBytes * 100 / totalBytes).toInt()
                        } else {
                            0
                        }
                        DownloadState.Progress(process)
                    }
                }
            }
        }

        return DownloadState.Failed("Download not found")
    }
}