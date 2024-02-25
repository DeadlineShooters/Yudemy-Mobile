package com.deadlineshooters.yudemy.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.deadlineshooters.yudemy.BuildConfig
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.DialogProgressBinding
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.LectureViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


open class BaseActivity : AppCompatActivity() {
    /**
     * Other activities can inherit this to use:
     * 1) loading animation (progress bar)
     */
    private lateinit var mProgressDialog : Dialog
    private lateinit var binding: DialogProgressBinding
    open lateinit var courseViewModel: CourseViewModel
    open lateinit var lecturerViewModel: LectureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Create global variable
        var config: HashMap<String, String> = HashMap()

    // Initialize cloudinary. Put in onCreate() function
        config["cloud_name"] = BuildConfig.CLOUD_NAME;
        MediaManager.init(this, config)


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


}