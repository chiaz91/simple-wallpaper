package com.cy.practice.wallpaper.ui.screen.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.cy.practice.wallpaper.domain.repository.PhotoRepository
import com.cy.practice.wallpaper.shared.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val wallpaperRepository: PhotoRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")

    val pagingPhotos = _query
        .flatMapLatest { query ->
            wallpaperRepository.getPagedPhotos(query).cachedIn(viewModelScope)
        }

    fun query(query: String = Constants.TOPICS.random()) {
        _query.value = query
    }
}

