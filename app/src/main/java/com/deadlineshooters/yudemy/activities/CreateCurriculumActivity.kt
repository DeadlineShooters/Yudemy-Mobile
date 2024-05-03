package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.SectionsAddedAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCreateCurriculumBinding
import com.deadlineshooters.yudemy.helpers.CustomItemTouchHelper
import com.deadlineshooters.yudemy.helpers.URIPathHelper
import com.deadlineshooters.yudemy.interfaces.ItemTouchListener
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.repositories.LectureRepository
import com.deadlineshooters.yudemy.repositories.SectionRepository
import com.google.android.material.snackbar.Snackbar


class CreateCurriculumActivity : BaseActivity() {
    private lateinit var sectionAdapter: SectionsAddedAdapter

    private lateinit var binding: ActivityCreateCurriculumBinding

    private var sectionWithLectures: ArrayList<SectionWithLectures> = arrayListOf()

    private var currentPositionsUpload = Pair(0, 0)

    private lateinit var course: Course

    private var deletedSections = arrayListOf<SectionWithLectures>()
    private var deletedLectures = arrayListOf<Lecture>()

    private var updatedSections = arrayListOf<Section>()
    private var updatedLectures = arrayListOf<Lecture>()

    private var addedSections = arrayListOf<SectionWithLectures>()
    private var addedLectures = arrayListOf<Lecture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCurriculumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        course = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra("course", Course::class.java)!!
        else
            intent.getParcelableExtra<Course>("course")!!

        sectionWithLectures = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableArrayListExtra("sections", SectionWithLectures::class.java) as ArrayList<SectionWithLectures>
            else
                intent.getParcelableArrayListExtra<SectionWithLectures>("sections") as ArrayList<SectionWithLectures>

        if(sectionWithLectures.isEmpty()) {
            SectionRepository().getSectionsWithLectures(course.id).addOnSuccessListener {
                sectionWithLectures = it as ArrayList<SectionWithLectures>
            }
        }

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

            addedSections.add(sectionWithLecture)
        }

        sectionAdapter.onAddLectureClick = { position ->
            Log.d("CreateCurriculumActivity", "onAddLectureClick itemCount before: ${sectionAdapter.lectureAdapters[position].itemCount}")

            val newLecture = Lecture("", sectionWithLectures[position].section._id, Video() , "", 0)
            (sectionWithLectures[position].lectures).add(newLecture)
            Log.d("CreateCurriculumActivity", "onAddLectureClick: ${sectionWithLectures[position].lectures}")
            Log.d("CreateCurriculumActivity", "onAddLectureClick itemCount after: ${sectionAdapter.lectureAdapters[position].itemCount}")
            sectionAdapter.lectureAdapters[position].notifyItemInserted(sectionAdapter.lectureAdapters[position].itemCount - 1)
            binding.addedSections.smoothScrollToPosition(position)

            if(!addedSections.contains(sectionWithLectures[position])) { // if add lecture to existing section
                addedLectures.add(newLecture)
            }
        }
        sectionAdapter.onDeleteSectionClick = {

            if(addedSections.contains(sectionWithLectures[it])) { // if delete added section
                addedSections.remove(sectionWithLectures[it])
            }
            else { // if delete existing section
                deletedSections.add(sectionWithLectures[it])
            }
            if(updatedSections.contains(sectionWithLectures[it].section)) { // if delete updated section
                updatedSections.remove(sectionWithLectures[it].section)
            }

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
            if(addedLectures.contains(sectionWithLectures[positionSection].lectures[positionLecture])) { // if delete added lecture
                addedLectures.remove(sectionWithLectures[positionSection].lectures[positionLecture])
            }
            else { // if delete existing lecture
                deletedLectures.add(sectionWithLectures[positionSection].lectures[positionLecture])
            }
            if(updatedLectures.contains(sectionWithLectures[positionSection].lectures[positionLecture])) { // if delete updated lecture
                updatedLectures.remove(sectionWithLectures[positionSection].lectures[positionLecture])
            }

            (sectionWithLectures[positionSection].lectures).removeAt(positionLecture)
            sectionAdapter.lectureAdapters[positionSection].notifyItemRemoved(positionLecture)
            sectionAdapter.lectureAdapters[positionSection].notifyItemRangeChanged(positionLecture, sectionAdapter.lectureAdapters[positionSection].itemCount - positionLecture)
            binding.addedSections.smoothScrollToPosition(positionSection)

            Log.d("CreateCurriculumActivity", "onDeleteLectureClick: ${sectionWithLectures[positionSection].lectures}")
        }

        sectionAdapter.onSectionTitleChange = {title, position ->
            sectionWithLectures[position].section.title = title

            if(!addedSections.contains(sectionWithLectures[position]) && !updatedSections.contains(sectionWithLectures[position].section)) {
                updatedSections.add(sectionWithLectures[position].section)
            }
        }
        sectionAdapter.onLectureTitleChange = {title, positionSection, positionLecture ->
            Log.d("CreateCurriculumActivity", "onLectureTitleChange: $positionSection $positionLecture")
            sectionWithLectures[positionSection].lectures[positionLecture].name = title

            if( !addedSections.contains(sectionWithLectures[positionSection])
                && !addedLectures.contains(sectionWithLectures[positionSection].lectures[positionLecture])
                && !updatedLectures.contains(sectionWithLectures[positionSection].lectures[positionLecture])
                ) {
                updatedLectures.add(sectionWithLectures[positionSection].lectures[positionLecture])
            }
        }

        sectionAdapter.onUploadVideoClick = {positionSection, positionLecture ->
            currentPositionsUpload = Pair(positionSection, positionLecture)

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))

            if( !addedSections.contains(sectionWithLectures[positionSection])
                && !addedLectures.contains(sectionWithLectures[positionSection].lectures[positionLecture])
                && !updatedLectures.contains(sectionWithLectures[positionSection].lectures[positionLecture])
                ) {
                updatedLectures.add(sectionWithLectures[positionSection].lectures[positionLecture])
            }
        }

        val callback: ItemTouchHelper.Callback =
            CustomItemTouchHelper(object : ItemTouchListener {
                override fun onMove(fromPosition: Int, toPosition: Int) {
                    sectionAdapter.onMove(fromPosition, toPosition)
                }

                override fun onSwipe(position: Int, direction: Int) {
                }
            })
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.addedSections)


        binding.btnSaveCurriculum.setOnClickListener {
            if(sectionWithLectures.isEmpty()) {
                Snackbar.make(binding.root, "Please add at least one lecture", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!checkIfFillAllFields()) {
                Snackbar.make(binding.root, "Please fill in all the titles and upload video for lectures", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(course.sectionList.isEmpty()) { // new course
                addCurriculum()
            }
            else {
                Log.d("CreateCurriculumActivity", "deletedSections: $deletedSections")
                Log.d("CreateCurriculumActivity", "deletedLectures: $deletedLectures")
                Log.d("CreateCurriculumActivity", "updatedSections: $updatedSections")
                Log.d("CreateCurriculumActivity", "updatedLectures: $updatedLectures")
                Log.d("CreateCurriculumActivity", "addedSections: $addedSections")
                Log.d("CreateCurriculumActivity", "addedLectures: $addedLectures")
                saveEditedCurriculum()
            }
        }
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            val uriPathHelper = URIPathHelper()
            val videoFullPath = uriPathHelper.getPath(baseContext, uri)
            Log.d("CreateCurriculumActivity", "onActivityResult: $videoFullPath")

            sectionWithLectures[currentPositionsUpload.first].lectures[currentPositionsUpload.second].content.contentUri = uri
            sectionWithLectures[currentPositionsUpload.first].lectures[currentPositionsUpload.second].content.secure_url = videoFullPath!!

            sectionAdapter.lectureAdapters[currentPositionsUpload.first].notifyItemChanged(currentPositionsUpload.second)
        } else {
            Log.d("PhotoPicker", "No media selected")
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
            if(sectionWithLectures.isNotEmpty() && checkIfAnyChanged()) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder
                    .setMessage("Are you sure you want to discard the changes?")
                    .setTitle("Please Confirm")
                    .setNegativeButton(Html.fromHtml("<font color='#5624D0'><b>Cancel</b></font>")) { dialog, which ->
                    }
                    .setPositiveButton(Html.fromHtml("<font color='#B32D0F'><b>Discard</b></font>")) { dialog, which ->
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

    private fun checkIfFillAllFields(): Boolean {
        for(section in sectionWithLectures) {
            if (section.section.title == "") {
                return false
            }
            if(section.lectures.isEmpty()) {
                return false
            }
            for(lecture in section.lectures) {
                if(lecture.name == ""
                    || (lecture.content.contentUri != null && (getVideoDuration(lecture.content.contentUri!!) == 0.0)))
                {
                    return false
                }
            }
        }
        return true
    }

    private fun addCurriculum() {
        showProgressDialog("Saving curriculum")

        SectionRepository()
            .addSectionsWithLecture(sectionWithLectures, course).addOnSuccessListener {
            hideProgressDialog()
            Toast.makeText(this, "Curriculum saved successfully", Toast.LENGTH_SHORT).show()
            onBack()
            }
            .addOnFailureListener {
                hideProgressDialog()
                Toast.makeText(this, "Failed to save curriculum. Please try again", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveEditedCurriculum() {
        showProgressDialog("Saving curriculum")
        SectionRepository()
            .addSectionsWithLecture(addedSections, course)
            .continueWithTask {
                LectureRepository().addLectures(addedLectures, course)
                    .continueWithTask {
                        SectionRepository().updateSections(updatedSections)
                            .continueWithTask {
                                LectureRepository().updateLectures(updatedLectures, course)
                                    .continueWithTask {
                                        SectionRepository().deleteSectionsWithLectures(deletedSections, course)
                                            .continueWithTask {
                                                LectureRepository().deleteLectures(deletedLectures, course)
                                                    .continueWithTask {
                                                        SectionRepository().updateIndexes(sectionWithLectures.filter {
                                                            !addedSections.contains(it) && !updatedSections.contains(it.section)
                                                        }.map { it.section })
                                                        .continueWithTask {
                                                            LectureRepository().updateIndexes(sectionWithLectures.flatMap { it.lectures }.filter {
                                                                !updatedLectures.contains(it) && !addedLectures.contains(it)
                                                            })
                                                        }
                                                    }
                                            }
                                    }
                            }
                    }
            }
            .addOnSuccessListener {
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

    fun checkIfAnyChanged(): Boolean {
        return deletedSections.isNotEmpty() || deletedLectures.isNotEmpty() || updatedSections.isNotEmpty() || updatedLectures.isNotEmpty() || addedSections.isNotEmpty() || addedLectures.isNotEmpty()
    }
}