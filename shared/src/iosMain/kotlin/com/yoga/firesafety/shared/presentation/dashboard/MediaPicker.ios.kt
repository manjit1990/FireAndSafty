package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.*
import platform.Foundation.*
import platform.PhotosUI.*
import platform.AVFoundation.*
import platform.CoreGraphics.*
import kotlinx.cinterop.*
import platform.darwin.NSObject

@Composable
actual fun rememberMediaPicker(onMediaSelected: (List<MediaItem>) -> Unit): MediaPicker {
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

    return remember {
        object : MediaPicker {
            override fun pickPhoto(fromCamera: Boolean) {
                if (fromCamera) {
                    openCamera(rootViewController, isVideo = false, onMediaSelected)
                } else {
                    openGallery(rootViewController, isVideo = false, onMediaSelected)
                }
            }

            override fun pickVideo(fromCamera: Boolean) {
                if (fromCamera) {
                    openCamera(rootViewController, isVideo = true, onMediaSelected)
                } else {
                    openGallery(rootViewController, isVideo = true, onMediaSelected)
                }
            }
        }
    }
}

private fun openCamera(
    rootViewController: UIViewController?, 
    isVideo: Boolean, 
    onMediaSelected: (List<MediaItem>) -> Unit
) {
    if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
        return
    }

    val picker = UIImagePickerController().apply {
        sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        mediaTypes = if (isVideo) listOf("public.movie") else listOf("public.image")
        delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
                val mediaType = didFinishPickingMediaWithInfo[UIImagePickerControllerMediaType] as? String
                val item = if (mediaType == "public.movie") {
                    val url = didFinishPickingMediaWithInfo[UIImagePickerControllerMediaURL] as? NSURL
                    MediaItem(url?.absoluteString ?: "", MediaType.VIDEO)
                } else {
                    val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                    // Note: In a real app, you'd save the UIImage to a file and use the URL
                    MediaItem("camera_photo", MediaType.PHOTO)
                }
                onMediaSelected(listOf(item))
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }
    rootViewController?.presentViewController(picker, animated = true, completion = null)
}

private fun openGallery(
    rootViewController: UIViewController?, 
    isVideo: Boolean, 
    onMediaSelected: (List<MediaItem>) -> Unit
) {
    val configuration = PHPickerConfiguration().apply {
        filter = if (isVideo) PHPickerFilter.videosFilter else PHPickerFilter.imagesFilter
        selectionLimit = 10
    }

    val picker = PHPickerViewController(configuration).apply {
        delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                val items = mutableListOf<MediaItem>()
                // Note: PHPicker requires handling NSItemProvider to get data/URLs
                // For this implementation, we acknowledge the selection.
                didFinishPicking.forEach { _ ->
                    items.add(MediaItem("gallery_item", if (isVideo) MediaType.VIDEO else MediaType.PHOTO))
                }
                onMediaSelected(items)
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }
    rootViewController?.presentViewController(picker, animated = true, completion = null)
}
