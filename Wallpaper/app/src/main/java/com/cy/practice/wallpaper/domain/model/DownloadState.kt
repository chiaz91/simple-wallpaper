package com.cy.practice.wallpaper.domain.model

import android.net.Uri

sealed class DownloadState {
    data class Progress(val progress: Int) : DownloadState()
    data class Success(val uri: Uri) : DownloadState()
    data class Failed(val reason: String) : DownloadState()
}