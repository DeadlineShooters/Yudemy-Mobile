package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityEditCourseLandingPageBinding
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.github.dhaval2404.imagepicker.ImagePicker

class EditCourseLandingPageActivity : BaseActivity() {
    private lateinit var binding: ActivityEditCourseLandingPageBinding
    private lateinit var course: Course
    private val courseRepository = CourseRepository()
    private var thumbnailUri : Uri? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditCourseLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionBar()

        binding.buttonUploadImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    // launch the image picker activity (implicit intent)
                    startForProfileImageResult.launch(intent)
                }
        }

        course = intent.getParcelableExtra("course", Course::class.java) ?: Course()

        // put data into this screen
        if (course.name == "") {
            binding.etCourseTitle.hint = getString(R.string.insert_your_course_title)
        }

        if (course.introduction == "") {
            binding.etCourseSubtitle.hint = getString(R.string.insert_subtitle)
        }

        if (course.description == "") {
            binding.etDesc.hint = getString(R.string.insert_your_course_description)
        }

        binding.etCourseTitle.setText(course.name)
        binding.etCourseSubtitle.setText(course.introduction)
        binding.etDesc.setText(course.description)

        Glide.with(this)
            .load(course.thumbnail.secure_url)
            .placeholder(R.drawable.placeholder)
            .into(binding.ivCourseImage)

        // handle save
        binding.btnSave.setOnClickListener {
            if(!checkIfFillAllFields()) {
                Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            course.name = binding.etCourseTitle.text.toString()
            course.introduction = binding.etCourseSubtitle.text.toString()
            course.description = binding.etDesc.text.toString()

            showProgressDialog("Please wait...")
            if (thumbnailUri is Uri) {
                CloudinaryHelper.uploadMedia(fileUri = thumbnailUri) {
                    course.thumbnail = it as Image
                    courseRepository.patchCourse(course)
                    Toast.makeText(this, "Course landing page updated", Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                    onBackUpdate()
                }
            } else {
                courseRepository.patchCourse(course)
                Toast.makeText(this, "Course landing page updated", Toast.LENGTH_SHORT).show()
                hideProgressDialog()
                onBackUpdate()
            }

            // Dismiss the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val filePath: String = fileUri.path!!

                // Handle file selected. filepath is at /storage/emulated/0/Android/data/com.deadlineshooters.yudemy/files/DCIM/IMG_20240225_093432622.png
                Log.d(
                    "ImagePicker",
                    filePath
                )  // Print the path of the file selected saved in local storage

                /** Crop image to 350 x 196 */
                // Convert the Uri to a Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, fileUri)

// Resize the Bitmap
                val dpWidth = 350
                val dpHeight = 196
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, dpWidth, dpHeight, false)

// Prepare to write the resized Bitmap to the MediaStore
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        "resized_image.png"
                    )
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES
                    )
                }

// Insert the metadata of the image into the MediaStore
                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                thumbnailUri = imageUri

// Write the resized Bitmap to the MediaStore
                resolver.openOutputStream(imageUri!!).use { out ->
                    if (out != null) {
                        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }

                binding.ivCourseImage.setImageURI(imageUri)


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun checkIfFillAllFields(): Boolean {
        return binding.etCourseTitle.text.toString().isNotBlank() &&
                binding.etCourseSubtitle.text.toString().isNotBlank() &&
                binding.etDesc.text.toString().isNotBlank() &&
                (course.thumbnail.secure_url != "" || (course.thumbnail.secure_url == "" && thumbnailUri != null))
    }

    private fun onBackUpdate() {
        val intent = Intent()
        intent.putExtra("course", course)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}