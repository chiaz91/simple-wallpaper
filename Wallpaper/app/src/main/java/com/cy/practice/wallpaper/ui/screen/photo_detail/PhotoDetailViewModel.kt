package com.cy.practice.wallpaper.ui.screen.photo_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.domain.model.DownloadState
import com.cy.practice.wallpaper.domain.repository.Downloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val downloader: Downloader,
) : ViewModel() {
    private val _downloadState = MutableStateFlow<DownloadState?>(null)
    val downloadState: StateFlow<DownloadState?> = _downloadState.asStateFlow()


    fun download(photo: PixabayPhoto) {
        val id = downloader.download(photo.largeImageUrl)

        viewModelScope.launch {
            downloader.observeDownload(id)
                .flowOn(Dispatchers.IO)
                .collect {
                    Timber.d("download[$id]:state=$it")
                    _downloadState.value = it
                }
        }
    }

}