package com.debopam.provider

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.debopam.ImagePickerActivity
import com.joinflatshare.FlatsharePhotoPicker.R

/**
 * Created by debopam on 01/03/23
 */
class AndroidPickerProvider(activity: ImagePickerActivity) :
    BaseProvider(activity) {

    fun startIntent() {
        val pickMedia =
            activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.e("ImagePicker", "Selected URI: $uri")
                    handleResult(uri)
                } else {
                    Log.e("ImagePicker", "Failed to get URI")
                    activity.finish()
                }
            }
        val mimeType = "image/*"
        pickMedia.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.SingleMimeType(
                    mimeType
                )
            )
        )
    }

    /**
     * This method will be called when final result fot this provider is enabled.
     */
    private fun handleResult(uri: Uri?) {
        if (uri != null) {
            takePersistableUriPermission(uri)
            activity.setImage(uri)
        } else {
            setError(R.string.error_failed_pick_gallery_image)
        }
    }

    /**
     * Take a persistable URI permission grant that has been offered. Once
     * taken, the permission grant will be remembered across device reboots.
     */
    private fun takePersistableUriPermission(uri: Uri) {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}