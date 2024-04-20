package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.SectionsAddedAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCreateCurriculumBinding
import com.deadlineshooters.yudemy.helpers.URIPathHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.repositories.SectionRepository
import com.google.android.material.snackbar.Snackbar
import java.io.File


class CreateCurriculumActivity : BaseActivity() {
    private lateinit var sectionAdapter: SectionsAddedAdapter

    private lateinit var binding: ActivityCreateCurriculumBinding

    private var sectionWithLectures: ArrayList<SectionWithLectures> = arrayListOf()
    private var originalSectionWithLectures: ArrayList<SectionWithLectures> = arrayListOf()

    private var currentPositionsUpload = Pair(0, 0)

    private var uploadedFiles = ArrayList<File>()

    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCurriculumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sectionWithLectures = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableArrayListExtra("sections", SectionWithLectures::class.java) as ArrayList<SectionWithLectures>
            else
                intent.getParcelableArrayListExtra<SectionWithLectures>("sections") as ArrayList<SectionWithLectures>

        originalSectionWithLectures = sectionWithLectures.clone() as ArrayList<SectionWithLectures>

        course = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra("course", Course::class.java)!!
        else
            intent.getParcelableExtra<Course>("course")!!

        setupActionBar()

        binding.parentAddedSectionsLayout.setOnTouchListener { v, event ->
            hideKeyboard(binding.root)
            false
        }

        sectionAdapter = SectionsAddedAdapter(sectionWithLectures)
        Log.d("CreateCurriculumActivity", "onCreate: $sectionWithLectures")
        binding.addedSections.adapter = sectionAdapter
        binding.addedSections.layoutManager = LinearLayoutManager(this)

        binding.btnAddSection.setOnClickListener {
            val sectionWithLecture = SectionWithLectures(Section("", "", 0), arrayListOf())
            sectionWithLectures.add(sectionWithLecture)
            Log.d("CreateCurriculumActivity", "onAddSectionClick: $sectionWithLectures")
            sectionAdapter.notifyItemInserted(sectionAdapter.itemCount - 1)
            binding.addedSections.smoothScrollToPosition(sectionAdapter.itemCount - 1)
        }

        sectionAdapter.onAddLectureClick = { position ->
            Log.d("CreateCurriculumActivity", "onAddLectureClick itemCount before: ${sectionAdapter.lectureAdapters[position].itemCount}")

            val newLecture = Lecture("", "", Video() , "", 0)
            (sectionWithLectures[position].lectures).add(newLecture)
            Log.d("CreateCurriculumActivity", "onAddLectureClick: ${sectionWithLectures[position].lectures}")
            Log.d("CreateCurriculumActivity", "onAddLectureClick itemCount after: ${sectionAdapter.lectureAdapters[position].itemCount}")

            sectionAdapter.lectureAdapters[position].notifyItemInserted(sectionAdapter.lectureAdapters[position].itemCount - 1)

            binding.addedSections.smoothScrollToPosition(position)
        }
        sectionAdapter.onDeleteSectionClick = {
            sectionWithLectures.removeAt(it)
            Log.d("CreateCurriculumActivity", "onDeleteSectionClick: $sectionWithLectures")
            sectionAdapter.lectureAdapters.removeAt(it)
            Log.d("CreateCurriculumActivity", "onDeleteSectionClick: lectureAdapters: ${sectionAdapter.lectureAdapters}")
            sectionAdapter.notifyItemRemoved(it)
            sectionAdapter.notifyItemRangeChanged(it, sectionAdapter.itemCount - it)

            if(sectionAdapter.itemCount != 0) {
                if(it == sectionAdapter.itemCount) {
                    binding.addedSections.smoothScrollToPosition(it - 1)
                }
                else {
                    binding.addedSections.smoothScrollToPosition(it)
                }
            }
        }

        sectionAdapter.onDeleteLectureClick = {positionLecture, positionSection ->
            (sectionWithLectures[positionSection].lectures).removeAt(positionLecture)
            sectionAdapter.lectureAdapters[positionSection].notifyItemRemoved(positionLecture)
            sectionAdapter.lectureAdapters[positionSection].notifyItemRangeChanged(positionLecture, sectionAdapter.lectureAdapters[positionSection].itemCount - positionLecture)
            binding.addedSections.smoothScrollToPosition(positionSection)

            Log.d("CreateCurriculumActivity", "onDeleteLectureClick: ${sectionWithLectures[positionSection].lectures}")
        }

        sectionAdapter.onSectionTitleChange = {title, position ->
            sectionWithLectures[position].section.title = title
        }
        sectionAdapter.onLectureTitleChange = {title, positionSection, positionLecture ->
            Log.d("CreateCurriculumActivity", "onLectureTitleChange: $positionSection $positionLecture")
            sectionWithLectures[positionSection].lectures[positionLecture].name = title
        }

        sectionAdapter.onUploadVideoClick = {positionSection, positionLecture ->
            val intent = Intent(Intent.ACTION_GET_CONTENT, null)
            currentPositionsUpload = Pair(positionSection, positionLecture)
            intent.type = "video/*"
            startPickVideoForResult.launch(intent)
        }

        binding.btnSaveCurriculum.setOnClickListener {
            if(checkIfSectionIsEmpty()) {
                Snackbar.make(binding.root, "Please fill in all the titles and upload video for lectures", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(course.id == "") { // new course
                CourseRepository().addCourse(course).addOnSuccessListener { courseId ->
                    if(courseId != "") {
                        saveCurriculum(courseId)
                    }
                    else {
                        Snackbar.make(
                            binding.root,
                            "Failed to save course. Please try again",
                            Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(Activity.RESULT_CANCELED, intent)
                        finish()
                    }
                }
            }
        }
    }

    private val startPickVideoForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data

            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val videoFullPath = uriPathHelper.getPath(baseContext, data.data!!)
                Log.d("CreateCurriculumActivity", "onActivityResult: $videoFullPath")
                if (videoFullPath != null) {
                    val file = File(videoFullPath)
                    uploadedFiles.add(file)

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(applicationContext, data.data)
                    val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val timeInMillisec = time!!.toLong()
                    retriever.release()

                    sectionWithLectures[currentPositionsUpload.first].lectures[currentPositionsUpload.second].content.duration = (timeInMillisec/1000).toDouble()
                    sectionWithLectures[currentPositionsUpload.first].lectures[currentPositionsUpload.second].content.contentUri = data.data

                    sectionAdapter.lectureAdapters[currentPositionsUpload.first].notifyItemChanged(currentPositionsUpload.second)
                }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCreateCurriculum)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarCreateCurriculum.setNavigationOnClickListener {
            if(sectionWithLectures.isNotEmpty()) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder
                    .setMessage("Are you sure you want to discard the changes?")
                    .setTitle("Please Confirm")
                    .setNegativeButton(Html.fromHtml("<font color='#363A43'><b>Cancel</b></font>")) { dialog, which ->

                    }
                    .setPositiveButton(Html.fromHtml("<font color='#7325A3'><b>Discard</b></font>")) { dialog, which ->
                        val intent = Intent()
                        setResult(Activity.RESULT_CANCELED, intent)
                        finish()
                    }
                    .create()
                    .show()
            }
            else {
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }
    }

    private fun checkIfSectionIsEmpty(): Boolean {
        if(sectionWithLectures.isEmpty()) {
            return true
        }
        for(section in sectionWithLectures) {
            if (section.section.title == "") {
                return true
            }
            if(section.lectures.isEmpty()) {
                return true
            }
            for(lecture in section.lectures) {
                if(lecture.name == "" || lecture.content.duration == 0.0) {
                    return true
                }
            }
        }
        return false
    }

    fun saveCurriculum(courseId: String) {
        showProgressDialog("Saving curriculum")
        course.id = courseId

        SectionRepository().addSectionsWithLecture(sectionWithLectures, course).addOnSuccessListener {
            hideProgressDialog()
            Toast.makeText(this, "Curriculum saved successfully", Toast.LENGTH_SHORT).show()
            onBack()
        }
            .addOnFailureListener {
                hideProgressDialog()
                Toast.makeText(this, "Failed to save curriculum. Please try again", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onBack() {
        val intent = Intent()
        intent.putParcelableArrayListExtra("sections", sectionWithLectures)
        intent.putExtra("course", course)
        Log.d("CreateCurriculumActivity", "onBackPressed: $sectionWithLectures")
        Log.d("CreateCurriculumActivity", "onBackPressed: $course")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}