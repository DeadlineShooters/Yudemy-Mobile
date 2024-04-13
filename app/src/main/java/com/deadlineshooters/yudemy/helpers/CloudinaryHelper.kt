package com.deadlineshooters.yudemy.helpers

import android.net.Uri
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.deadlineshooters.yudemy.BuildConfig
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Media
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.utils.Constants

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

    fun uploadToCloudinary(filepath: String, callback: (Image?) -> Unit) {
        Log.d("cloudinary", BuildConfig.UPLOAD_PRESET)

        MediaManager.get().upload(filepath)
            .unsigned(BuildConfig.UPLOAD_PRESET)
            .option("folder", Constants.CLOUDINARY_FOLDER)
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val secureUrl = resultData?.get("secure_url").toString()
                    val publicId = resultData?.get("public_id").toString()
                    val image = Image(secureUrl, publicId)
                    callback(image) // Truyền kết quả đến hàm gọi lại
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    val progress = bytes.toDouble() / totalBytes
                    // Xử lý tiến trình nếu cần
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.d("cloudinary", "Task rescheduled")
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.d("cloudinary", "Task Not successful: $error")
                    // Xử lý lỗi nếu cần
                    callback(null) // Truyền null để báo lỗi
                }

                override fun onStart(requestId: String?) {
                    Log.d("cloudinary", "Start")
                }
            }).dispatch()
    }

//    CloudinaryHelper().uploadToCloudinary(image) { image ->
////                    if(image != null){
////                        val imageUrl = image.secure_url
////                        val publicId = image.public_id
////                        val image = Image(publicId, imageUrl)
////                        Log.d("Image", image.toString())
////                        imageArray.add(image)
////                    }
////                }

    fun uploadImageListToCloudinary(imageList: ArrayList<String>, callback: (ArrayList<Image>) -> Unit) {
        val resultList = ArrayList<Image>()
        var count = 0
        for (image in imageList) {
            uploadToCloudinary(image) {
                if (it != null) {
                    resultList.add(it)
                    count++
                    if (count == imageList.size) {
                        callback(resultList)
                    }
                }
            }
        }
    }
}