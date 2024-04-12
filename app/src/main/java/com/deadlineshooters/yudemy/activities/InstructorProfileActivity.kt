package com.deadlineshooters.yudemy.activities

import InstructorCourseListAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel

class InstructorProfileActivity : BaseActivity() {
    private lateinit var backBtn: Button
    private lateinit var instructorName: TextView
    private lateinit var instructorHeadline: TextView
    private lateinit var instructorTotalStudents: TextView
    private lateinit var instructorTotalReviews: TextView
    private lateinit var instructorBio: TextView
    private lateinit var showMoreBtn: TextView
    private lateinit var instructorImage: ImageView

    private var isExpanded = true
    private lateinit var instructorCourseListAdapter: InstructorCourseListAdapter
    private lateinit var instructorCoursesOverview: TextView
    private lateinit var instructorCoursesView: RecyclerView
    private lateinit var seeAllBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_profile)

        val instructorId = intent.getStringExtra("instructorId") ?: ""
        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]
        instructorViewModel.getInstructorById(instructorId)

        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        courseViewModel.getTop3InstructorCourseList(instructorId)

        backBtn = findViewById(R.id.backBtn6)
        instructorName = findViewById(R.id.instructorName)
        instructorHeadline = findViewById(R.id.instructorHeadline)
        instructorTotalStudents = findViewById(R.id.instructorTotalStudents)
        instructorTotalReviews = findViewById(R.id.instructorTotalReviews)
        instructorBio = findViewById(R.id.instructorBio)
        instructorImage= findViewById(R.id.instructorImage)
        showMoreBtn = findViewById(R.id.showMoreBtn)
        instructorCoursesOverview = findViewById(R.id.instructorCoursesOverview)
        instructorCoursesView = findViewById(R.id.instructorCoursesView)
        seeAllBtn = findViewById(R.id.seeAllBtn)

        instructorViewModel.instructor.observe(this, Observer { it ->
            instructorName.text = it.fullName
            instructorHeadline.text = it.instructor!!.headline
            instructorTotalStudents.text = it.instructor!!.totalStudents.formatThousands()
            instructorTotalReviews.text = it.instructor!!.totalReviews.formatThousands()
            instructorBio.text = it.instructor!!.bio
            ImageViewHelper().setImageViewFromUrl(it.image, instructorImage)
        })

        showMoreBtn.setOnClickListener{
            if(isExpanded){
                instructorBio.maxLines = Integer.MAX_VALUE
                showMoreBtn.text = "Show less"
            } else {
                instructorBio.maxLines = 7
                showMoreBtn.text = "Show more"
            }
            isExpanded = !isExpanded
        }

        courseViewModel.courses.observe(this, Observer { it ->
            instructorCoursesOverview.text = "My course (${it.size.toString()})"

            instructorCourseListAdapter = InstructorCourseListAdapter(it, this)
            instructorCoursesView.adapter = instructorCourseListAdapter
            instructorCoursesView.layoutManager = LinearLayoutManager(this)

            instructorCourseListAdapter.onItemClick = { course ->
                val intent = Intent(this, CourseDetailActivity::class.java)
                intent.putExtra("courseId", course.id)
                startActivity(intent)
            }
        })


        seeAllBtn.setOnClickListener {
            //TODO: make event to see all courses taught by the instructor
            val intent = Intent(this, InstructorAllCoursesActivity::class.java)
            intent.putExtra("instructorId", instructorId)
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            //TODO: make event to go back to previous activity
            finish()
        }
    }
}