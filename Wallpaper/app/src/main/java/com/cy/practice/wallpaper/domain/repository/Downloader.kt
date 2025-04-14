package com.cy.practice.wallpaper.domain.repository

import com.cy.practice.wallpaper.domain.model.DownloadState
import kotlinx.coroutines.flow.Flow

interface Downloader {
    fun download(url: String, fileName: String? = null): Long
    fun observeDownload(jobId: Long, pollIntervalMs: Long = 500L): Flow<DownloadState>
}