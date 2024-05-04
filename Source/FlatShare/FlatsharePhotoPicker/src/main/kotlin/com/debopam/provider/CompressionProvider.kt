package com.debopam.provider

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.debopam.ImagePicker
import com.debopam.ImagePickerActivity
import com.debopam.util.FileUtil
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File


/**
 * Compress Selected/Captured Image
 *
 * @author Dhaval Patel
 * @version 1.0
 * @since 04 January 2019
 */
class CompressionProvider(activity: ImagePickerActivity) : BaseProvider(activity) {

    companion object {
        private val TAG = CompressionProvider::class.java.simpleName
    }

    private val mMaxWidth: Int
    private val mMaxHeight: Int
    private val mMaxFileSize: Long

    private val mFileDir: File

    init {
        val bundle = activity.intent.extras ?: Bundle()

        // Get Max Width/Height parameter from Intent
        mMaxWidth = bundle.getInt(ImagePicker.EXTRA_MAX_WIDTH, 0)
        mMaxHeight = bundle.getInt(ImagePicker.EXTRA_MAX_HEIGHT, 0)

        // Get Maximum Allowed file size
        mMaxFileSize = bundle.getLong(ImagePicker.EXTRA_IMAGE_MAX_SIZE, 0)

        // Get File Directory
        val fileDir = bundle.getString(ImagePicker.EXTRA_SAVE_DIRECTORY)
        mFileDir = getFileDir(fileDir)
    }

    /**
     * Check if compression should be enabled or not
     *
     * @return Boolean. True if Compression should be enabled else false.
     */
    private fun isCompressEnabled(): Boolean {
        return mMaxFileSize > 0L
    }

    /**
     * Check if compression is required
     * @param file File object to apply Compression
     */
    private fun isCompressionRequired(file: File): Boolean {
        val status = isCompressEnabled() && getSizeDiff(file) > 0L
        if (!status && mMaxWidth > 0 && mMaxHeight > 0) {
            // Check image resolution
            val resolution = FileUtil.getImageResolution(file)
            return resolution.first > mMaxWidth || resolution.second > mMaxHeight
        }
        return status
    }

    /**
     * Check if compression is required
     * @param uri Uri object to apply Compression
     */
    fun isCompressionRequired(uri: Uri): Boolean {
        val status = isCompressEnabled() && getSizeDiff(uri) > 0L
        if (!status && mMaxWidth > 0 && mMaxHeight > 0) {
            // Check image resolution
            val resolution = FileUtil.getImageResolution(this, uri)
            return resolution.first > mMaxWidth || resolution.second > mMaxHeight
        }
        return status
    }

    private fun getSizeDiff(file: File): Long {
        return file.length() - mMaxFileSize
    }

    private fun getSizeDiff(uri: Uri): Long {
        val length = FileUtil.getImageSize(this, uri)
        return length - mMaxFileSize
    }

    /**
     * Compress given file if enabled.
     *
     * @param uri Uri to compress
     */
    fun compress(uri: Uri) {
        startCompressionWorker(uri)
    }

    /**
     * Start Compression in Background
     */
    private fun startCompressionWorker(uri: Uri) {
        val pd = ProgressDialog(this@CompressionProvider)
        pd.setMessage("Please wait while the image is compressed")
        pd.show()
        val file = FileUtil.getTempFile(this@CompressionProvider, uri)
        if (file != null) {
            activity.lifecycleScope.launch {
                val compressedFile =
                    Compressor.compress(this@CompressionProvider, file) {
                        quality(80)
                        format(Bitmap.CompressFormat.JPEG)
                        size(mMaxFileSize)
                    }
                pd.dismiss()
                handleResult(compressedFile)
            }
        }
    }

    /**
     * This method will be called when final result fot this provider is enabled.
     */
    private fun handleResult(file: File) {
        activity.setCompressedImage(Uri.fromFile(file))
    }
}
