package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.runtime.Composable

@Composable
expect fun rememberMediaPicker(
    onMediaSelected: (List<MediaItem>) -> Unit
): MediaPicker

interface MediaPicker {
    fun pickPhoto(fromCamera: Boolean)
    fun pickVideo(fromCamera: Boolean)
}
