package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.ui.graphics.ImageBitmap

enum class MediaType {
    PHOTO, VIDEO
}

data class MediaItem(
    val uri: String,
    val type: MediaType,
    val thumbnail: ImageBitmap? = null
)
