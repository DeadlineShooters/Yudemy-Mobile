package com.deadlineshooters.yudemy.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.DialogProgressBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


open class BaseActivity : AppCompatActivity() {
    /**
     * Other activities can inherit this to use:
     * 1) loading animation (progress bar)
     */
    private lateinit var mProgressDialog : Dialog
    private lateinit var binding: DialogProgressBinding
    open lateinit var courseViewModel: CourseViewModel
    open lateinit var instructorViewModel: InstructorViewModel

    val MSG_DISMISS_DIALOG = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        binding.tvProgressText.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getCurrentUserEmail(): String {
        return FirebaseAuth.getInstance().currentUser!!.email.toString()
    }

    /**
     * message is the error message to show in the snackbar.
     */
    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }
    fun Int.formatThousands(): String {
        val regex = "(\\d)(?=(\\d{3})+\$)".toRegex()
        return this.toString().replace(regex, "$1.")
    }

    fun Long.formatThousands(): String {
        val regex = "(\\d)(?=(\\d{3})+\$)".toRegex()
        return this.toString().replace(regex, "$1.")
    }

    fun Double.formatThousands(): String {
        val regex = "(\\d)(?=(\\d{3})+\\.)".toRegex()
        return this.toString().replace(regex, "$1.")
    }

    fun Float.formatThousands(): String {
        val regex = "(\\d)(?=(\\d{3})+\\.)".toRegex()
        return this.toString().replace(regex, "$1.")
    }

    fun saveBitmapToFile(bitmap: Bitmap, context: Context): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JPEG_$timeStamp.jpg"
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, fileName)

        return try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun showNoButtonDialogWithTimeout(context: Context, title: String, content: String ,timeout: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_custom)

        val mTitle = dialog.findViewById(R.id.title_dialog) as TextView
        val mContent = dialog.findViewById(R.id.content_dialog) as TextView

        mTitle.text = title
        mContent.text = content

        dialog.show()

        val handler = android.os.Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, timeout.toLong())
    }


    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getHourFromIdx(idx: Int): Int {
        val hours = arrayListOf(6, 9, 12, 15, 18, 21)
        return hours[idx]
    }

    fun getDayFromIdx(idx: Int): Int {
        return when(idx) {
            0 -> Calendar.MONDAY
            1 -> Calendar.TUESDAY
            2 -> Calendar.WEDNESDAY
            3 -> Calendar.THURSDAY
            4 -> Calendar.FRIDAY
            5 -> Calendar.SATURDAY
            6 -> Calendar.SUNDAY
            else -> -1
        }
    }

    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap?
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = null
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    fun retriveVideoFrameFromVideo(uri: Uri?): Bitmap? { // For local videos
        var bitmap: Bitmap?
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(applicationContext, uri)
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = null
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    fun getVideoDuration(uri: Uri): Double {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(applicationContext, uri)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInSec = ((time?.toLong() ?: 0)/1000).toDouble()
        retriever.release()
        return timeInSec
    }
}