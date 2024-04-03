package com.deadlineshooters.yudemy.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import com.deadlineshooters.yudemy.models.Image
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.Executors

class ImageViewHelper {
    fun setImageViewFromUrl(image: Image, view: ImageView) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            try {
                val iStream = java.net.URL(image.secure_url).openStream()
                var imageBitmap = BitmapFactory.decodeStream(iStream)

                handler.post {
                    view.setImageBitmap(imageBitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, UUID.randomUUID().toString(), null)
        return Uri.parse(path)
    }
}