package com.cy.practice.wallpaper.ui.screen.photo_detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cy.practice.wallpaper.domain.model.DownloadState
import com.cy.practice.wallpaper.domain.model.DownloadState.Progress
import com.cy.practice.wallpaper.domain.model.DownloadState.Success
import com.cy.practice.wallpaper.shared.onDebounceClick


@Composable
fun DownLoadButton(
    state: DownloadState?,
    onDownLoad: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(iconSize)
            .wrapContentSize(Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onDebounceClick {
                onDownLoad()
            },
            enabled = state !is Progress,
            modifier = Modifier.size(iconSize)
        ) {
            when (state) {
                is Success -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Downloaded",
                        tint = Color(0xFF4CAF50)
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download"
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = state is Progress,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            val progress = (state as? Progress)?.progress ?: 0
            LinearProgressIndicator(
//                progress = { progress / 100f },
            )
        }
    }

}