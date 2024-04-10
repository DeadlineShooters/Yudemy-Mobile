package com.deadlineshooters.yudemy.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.os.Message
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.DialogProgressBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.Handler
import java.util.logging.LogRecord


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

        /** add dummy data */
        // Course
//        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
//        viewModel.addDummyCourse()

        // Lecturer
//        lecturerViewModel = ViewModelProvider(this).get(LectureViewModel::class.java)
//        lecturerViewModel.addDummyLecturer()
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

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

//    fun getCurUser(): User {
//        return curUser
//    }
//
//    fun setCurrentUser(user: User) {
//        curUser = user
//    }

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
}