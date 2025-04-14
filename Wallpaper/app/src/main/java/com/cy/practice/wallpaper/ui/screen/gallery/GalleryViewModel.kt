package com.cy.practice.wallpaper.ui.screen.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cy.practice.wallpaper.data.remote.dto.PixabayPhoto
import com.cy.practice.wallpaper.domain.repository.PhotoRepository
import com.cy.practice.wallpaper.shared.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val wallpaperRepository: PhotoRepository
) : ViewModel() {
    private val _photos: MutableStateFlow<List<PixabayPhoto>> = MutableStateFlow(emptyList())
    val photos = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _isLoading.value = true
        viewModelScope.launch {
            delay(1000)
            val result = wallpaperRepository.getPagedPhotos()
            _isLoading.value = false
            if (result is ApiResult.Success) {
                _photos.value = result.data
            }
        }
    }
}

