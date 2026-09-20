package com.yoga.firesafety.shared.presentation.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

@Composable
actual fun rememberMediaPicker(onMediaSelected: (List<MediaItem>) -> Unit): MediaPicker {
    val context = LocalContext.current
    var tempUriString by rememberSaveable { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val items = uris.map { uri ->
                val type = if (context.contentResolver.getType(uri)?.startsWith("video") == true) MediaType.VIDEO else MediaType.PHOTO
                MediaItem(uri.toString(), type, getThumbnail(context, uri, type))
            }
            onMediaSelected(items)
        }
    }

    val cameraPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUriString?.let { uriStr ->
                onMediaSelected(listOf(MediaItem(uriStr, MediaType.PHOTO, getThumbnail(context, uriStr.toUri(), MediaType.PHOTO))))
            }
        }
    }

    val cameraVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            tempUriString?.let { uriStr ->
                onMediaSelected(listOf(MediaItem(uriStr, MediaType.VIDEO, getThumbnail(context, uriStr.toUri(), MediaType.VIDEO))))
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Optional: Handle permission denied UX
        }
    }

    return remember {
        object : MediaPicker {
            override fun pickPhoto(fromCamera: Boolean) {
                if (fromCamera) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        val file = createTempFile(context, ".jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        tempUriString = uri.toString()
                        cameraPhotoLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                } else {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }

            override fun pickVideo(fromCamera: Boolean) {
                if (fromCamera) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        val file = createTempFile(context, ".mp4")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        tempUriString = uri.toString()
                        cameraVideoLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                } else {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                }
            }
        }
    }
}

private fun createTempFile(context: Context, extension: String): File {
    val directory = context.externalCacheDir ?: context.cacheDir
    return File.createTempFile("capture_", extension, directory)
}

private fun getThumbnail(context: Context, uri: Uri, type: MediaType): ImageBitmap? {
    return try {
        val bitmap = if (type == MediaType.VIDEO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(uri, Size(512, 512), null)
            } else {
                null
            }
        } else {
            context.contentResolver.openInputStream(uri).use {
                BitmapFactory.decodeStream(it)
            }
        }
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
