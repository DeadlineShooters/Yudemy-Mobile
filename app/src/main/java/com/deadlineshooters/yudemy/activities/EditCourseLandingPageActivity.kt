package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityEditCourseLandingPageBinding
import com.deadlineshooters.yudemy.dialogs.PreviewCourseDialog
import com.deadlineshooters.yudemy.fragments.CategoryFragment
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.helpers.DialogHelper
import com.deadlineshooters.yudemy.helpers.URIPathHelper
import com.deadlineshooters.yudemy.models.Category
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.repositories.CategoryRepository
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.viewmodels.CategoryViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File

class EditCourseLandingPageActivity : BaseActivity(), CategoryFragment.DialogListener {
    private lateinit var binding: ActivityEditCourseLandingPageBinding
    private lateinit var course: Course
    private val courseRepository = CourseRepository()
    private val categoryRepository = CategoryRepository()
    private var thumbnailUri: Uri? = null
    private var isEdited = false
    private lateinit var categoryViewModel: CategoryViewModel
    private var categories: List<Category> = listOf()
    private var currentCategoryIndex: Int = -1
    private var isImageEdited = false
    private var isVideoEdited = false
    private var uploadedVideo: File? = null
    private var previewVideo = Video()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditCourseLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.isEnabled = false


        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        categoryViewModel.refreshCategories()

        categoryViewModel.categoryList.observe(this, Observer { loadedCategories ->
            categories = loadedCategories
            currentCategoryIndex = categories.indexOfFirst { it._id == course.category }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionBar()

        binding.etCourseTitle.addTextChangedListener {
            if (it.toString() != course.name) {
                isEdited = true
                binding.btnSave.isEnabled = true
            }
        }

        binding.etCourseSubtitle.addTextChangedListener {
            if (it.toString() != course.introduction) {
                isEdited = true
                binding.btnSave.isEnabled = true
            }
        }

        binding.etDesc.addTextChangedListener {
            if (it.toString() != course.description) {
                isEdited = true
                binding.btnSave.isEnabled = true
            }
        }




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

        binding.buttonUploadVideo.setOnClickListener {
            DialogHelper.showProgressDialog(this, "Processing video...")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"

// Launch the file picker and handle the result
            startPickVideoForResult.launch(intent)
        }


        loadCourseInformation()
        DialogHelper.hideProgressDialog()


        // handle save
        fun handleSuccess() {
            DialogHelper.hideProgressDialog()

            Toast.makeText(this, "Course landing page updated", Toast.LENGTH_SHORT).show()
            loadCourseInformation()
            DialogHelper.hideProgressDialog()

        }

        binding.btnSave.setOnClickListener {
            // Dismiss the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (!isEdited && !isImageEdited && !isVideoEdited) {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            course.name = binding.etCourseTitle.text.toString()
            course.introduction = binding.etCourseSubtitle.text.toString()
            course.description = binding.etDesc.text.toString()

            if (thumbnailUri is Uri) {
                DialogHelper.showProgressDialog(this, "Saving video...")
                CloudinaryHelper.uploadMedia(fileUri = thumbnailUri) {
                    course.thumbnail = it as Image
                    courseRepository.patchCourse(course) {
                        handleSuccess()
                    }
                }
            }

            if (uploadedVideo != null) {
                DialogHelper.showProgressDialog(this, "Saving video...")

                CloudinaryHelper.uploadMedia(
                    fileUri = Uri.fromFile(uploadedVideo),
                    isVideo = true
                ) {
                    course.promotionalVideo = it as Video
                    courseRepository.patchCourse(course) {
                        handleSuccess()
                    }
                }
            }

            if (thumbnailUri !is Uri && uploadedVideo == null) {
                courseRepository.patchCourse(course) {
                    handleSuccess()
                }
            }

            isEdited = false
            isImageEdited = false
            isVideoEdited = false
            binding.btnSave.isEnabled = false
        }


        binding.buttonShowCategoryDialog.setOnClickListener {
            categoryViewModel.refreshCategories()

            DialogHelper.showProgressDialog(this, "Loading categories")
            val dialog = CategoryFragment(currentCategoryIndex)
            dialog.listener = this
            dialog.show(supportFragmentManager, "FireMissilesDialogFragment2")
        }

        categoryRepository.loadCategory(course.category) { category ->
            if (category != null) {
                binding.buttonShowCategoryDialog.text = category.name
            } else
                binding.buttonShowCategoryDialog.text = getString(R.string.select_category)

        }
    }

    private val startPickVideoForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data

                if (data?.data != null) {
                    val uriPathHelper = URIPathHelper()
                    val videoFullPath = uriPathHelper.getPath(baseContext, data.data!!)
                    Log.d("UploadVideo", "onActivityResult: $videoFullPath")
                    if (videoFullPath != null) {
                        val file = File(videoFullPath)
                        uploadedVideo = file

                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(applicationContext, data.data)
                        val time =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        val timeInMillisec = time!!.toLong()

                        previewVideo.duration = (timeInMillisec / 1000).toDouble()
                        previewVideo.contentUri = data.data

                        // glide the video thumbnail
                        val frame = retriever.getFrameAtTime(
                            1000,
                            MediaMetadataRetriever.OPTION_CLOSEST
                        ) // get frame at 1 second
                        retriever.release()


                        isVideoEdited = true
                        binding.btnSave.isEnabled = true

                        binding.ivCourseVideo.visibility = View.INVISIBLE
                        binding.tvInstructionSaveVideo.visibility = View.VISIBLE
                        retriever.release()

                    }
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
            DialogHelper.hideProgressDialog()

        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        // Reload course information on resume
        loadCourseInformation()
        DialogHelper.hideProgressDialog()
//
        categoryViewModel.refreshCategories()

        categoryViewModel.categoryList.observe(this, Observer { loadedCategories ->
            categories = loadedCategories
            currentCategoryIndex = categories.indexOfFirst { it._id == course.category }
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loadCourseInformation() {
        DialogHelper.showProgressDialog(this, "Loading course...")

        course = intent.getParcelableExtra("course", Course::class.java) ?: Course()
        courseRepository.getCourseById(courseId = course.id) { courseLoaded ->

            course = courseLoaded!!

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

            // Load video frame
            if (course.promotionalVideo.secure_url != "") {
                binding.ivPlay.visibility = View.VISIBLE
                binding.ivCourseVideo.visibility = View.VISIBLE
                binding.tvInstructionSaveVideo.visibility = View.INVISIBLE

                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(course.promotionalVideo.secure_url, HashMap())
                val bitmap = retriever.getFrameAtTime(
                    1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                ) // get frame at second 1
                retriever.release()

                Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.ivCourseVideo)


                binding.ivCourseVideo.setOnClickListener {
                    val vidPath = course.promotionalVideo.secure_url
                    if (vidPath != "") {
                        val previewCourseDialog = PreviewCourseDialog(vidPath)
                        previewCourseDialog.show(supportFragmentManager, "PreviewCourseDialog")
                    } else
                        Toast.makeText(this, "No promotional video available", Toast.LENGTH_SHORT)
                            .show()

                }
            }


            // Load category information
            categoryRepository.loadCategory(course.category) { category ->
                if (category != null) {
                    binding.buttonShowCategoryDialog.text = category.name
                } else {
                    binding.buttonShowCategoryDialog.text = getString(R.string.select_category)
                }
            }

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

// Set the ImageView directly with the Bitmap
                binding.ivCourseImage.setImageBitmap(resizedBitmap)
                isImageEdited = true
                binding.btnSave.isEnabled = true


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
            if (isEdited || isVideoEdited || isImageEdited) {
                AlertDialog.Builder(this)
                    .setTitle("Discard changes?")
                    .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                    .setPositiveButton("Discard") { _, _ ->
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                finish()
            }
        }

    }


    override fun onDialogPositiveClick(
        dialog: DialogFragment,
        selectedCategory: Category,
        selectedCategoryIndex: Int
    ) {
        binding.buttonShowCategoryDialog.text = selectedCategory.name
        course.category = selectedCategory._id

        currentCategoryIndex = selectedCategoryIndex
    }


}