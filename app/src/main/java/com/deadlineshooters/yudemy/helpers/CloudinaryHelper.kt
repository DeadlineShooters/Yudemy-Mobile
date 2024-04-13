package com.deadlineshooters.yudemy.helpers

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.UploadPolicy
import com.cloudinary.utils.ObjectUtils
import com.deadlineshooters.yudemy.BuildConfig
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Media
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.utils.Constants
import com.github.dhaval2404.imagepicker.ImagePicker

class CloudinaryHelper {
    companion object {

        fun uploadMedia(filepath: String? = null, fileUri: Uri? = null, isVideo: Boolean = false, callback: (Media) -> Unit) {
            Log.d("cloudinary", BuildConfig.UPLOAD_PRESET)
            val cloudinary = Cloudinary(
                ObjectUtils.asMap(
                    "cloud_name", BuildConfig.CLOUD_NAME,
                    "api_key", BuildConfig.API_KEY,
                    "api_secret", BuildConfig.API_SECRET
                )
            )

            val uploadRequest = when {
                filepath != null -> MediaManager.get().upload(filepath)
                fileUri != null -> MediaManager.get().upload(fileUri)
                else -> throw IllegalArgumentException("Either filepath or fileUri must be provided.")
            }

            uploadRequest
                .option("resource_type", if (isVideo) "video" else "image")
                .unsigned(BuildConfig.UPLOAD_PRESET).option("folder", Constants.CLOUDINARY_FOLDER)
                .callback(object : UploadCallback {
                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?
                    ) {
                        Log.d("cloudinary", "Uploaded media successfully\n$resultData")
                        var result = if (isVideo) {
                            Video(
                                resultData?.get("secure_url").toString(),
                                resultData?.get("public_id").toString(),
                                resultData?.get("resource_type").toString(),
                                resultData?.get("duration") as Double
                            )
                        } else {
                            Image(
                                resultData?.get("secure_url").toString(),
                                resultData?.get("public_id").toString()
                            )
                        }

                        callback(result)
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        val progress = bytes.toDouble() / totalBytes

                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.d("cloudinary", "Task rescheduled")

                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.d("cloudinary", "Task Not successful: $error")

                        if (error != null) {
                            Log.e(
                                "t",
                                "Upload failed. Error description: ${error.description}, Code: ${error.code}"
                            )
                        } else {
                            Log.e("t", "Upload failed with unknown error")
                        }
                    }

                    override fun onStart(requestId: String?) {
                        Log.d("cloudinary", "Start")
                    }
                }).dispatch()

        }
    }

}