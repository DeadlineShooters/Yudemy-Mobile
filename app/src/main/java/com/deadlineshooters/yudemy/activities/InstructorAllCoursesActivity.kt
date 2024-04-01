package com.deadlineshooters.yudemy.activities

import InstructorCourseListAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel

class InstructorAllCoursesActivity : BaseActivity() {
    private lateinit var backBtn: Button
    private lateinit var textView40: TextView
    private lateinit var instructorAllCoursesView: RecyclerView

//    private val dumpCourse1 = Course("123", "Become a Web Developer: 2024 Bootcamp", "Brad Schiff", 1000, "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", 199.99, arrayListOf("Introduction", "HTML", "CSS", "Javascript", "Node", "React", "MongoDB", "Final Project"), Video("secure_url", "public_id"), "English", "Web Development", 100, 8, 10000, 10, Image("secure_url", "public_id"), 5, 4.7,true, "12/12/2021", 100000)
//    private val dumpCourse2 = Course("124", "Let's Learn Laravel: A Guided Path For Beginners", "Brad Schiff", 1000, "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", 199.99, arrayListOf("Introduction", "HTML", "CSS", "Javascript", "Node", "React", "MongoDB", "Final Project"), Video("secure_url", "public_id"), "English", "Web Development", 100, 8, 10000, 10, Image("secure_url", "public_id"), 3, 4.7, true, "12/12/2021", 100000)
//    private val dumpCourse3 = Course("125", "MySQL For Beginners: Real Database Experience Real Fast", "Brad Schiff", 1000, "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", 199.99, arrayListOf("Introduction", "HTML", "CSS", "Javascript", "Node", "React", "MongoDB", "Final Project"), Video("secure_url", "public_id"), "English", "Web Development", 100, 8, 10000, 10, Image("secure_url", "public_id"), 5, 4.7,true, "12/12/2021", 100000)
//    private val dumpCourseList = arrayListOf(dumpCourse1, dumpCourse2, dumpCourse3)
//    private var instructorCourseListAdapter = InstructorCourseListAdapter(dumpCourseList)

    private lateinit var instructorCourseListAdapter: ArrayList<Course>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_all_courses)

        val instructorId = intent.getStringExtra("instructorId") ?: ""

        backBtn = findViewById(R.id.backBtn15)
        textView40 = findViewById(R.id.textView40)
        instructorAllCoursesView = findViewById(R.id.instructorAllCoursesView)

        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        courseViewModel.getInstructorCourseList(instructorId)

        backBtn.setOnClickListener {
            finish()
        }

        courseViewModel.courses.observe(this, Observer { it ->
            instructorCourseListAdapter = it as ArrayList<Course>
            instructorAllCoursesView.adapter = InstructorCourseListAdapter(instructorCourseListAdapter, this)
            instructorAllCoursesView.layoutManager = LinearLayoutManager(this)
        })
//
//        instructorCourseListAdapter = InstructorCourseListAdapter(dumpCourseList)
//        instructorAllCoursesView.adapter = instructorCourseListAdapter
//        instructorAllCoursesView.layoutManager = LinearLayoutManager(this)

    }
}