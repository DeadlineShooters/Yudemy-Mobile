package com.deadlineshooters.yudemy.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.InstructorCourseListAdapter
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.Video

class InstructorProfileActivity : BaseActivity() {
    private lateinit var backBtn: Button
    private lateinit var instructorName: TextView
    private lateinit var instructorHeadline: TextView
    private lateinit var instructorTotalStudents: TextView
    private lateinit var instructorTotalReviews: TextView
    private lateinit var instructorBio: TextView
    private lateinit var showMoreBtn: TextView
    private val dumpBio = "Hi, I'm Brad and I've taught web development to countless coworkers and held training sessions for fortune 100 companies.\n\n" +
            "I also teach local night classes and run a somewhat popular web development tutorial YouTube channel named LearnWebCode.\n\n" +
            "I'm a front-end developer, designer, and educator. I've been building user interfaces for over a decade for the world's largest brands, international technology leaders, and national political campaigns.\n\n" +
            "I'm fortunate to enjoy the development work I do, but my true passion is helping people learn."
    private val dumpInst = Instructor("123", "Brad", "Schiff", "Web developer", 72087, 247011,dumpBio, Image("secure_url", "public_id"), "walletId", "walletName")
    private var isExpanded = true
    private val dumpCourse1 = Course("123", "Become a Web Developer: 2024 Bootcamp", "Brad Schiff", 1000, "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", 199.99, arrayListOf("Introduction", "HTML", "CSS", "Javascript", "Node", "React", "MongoDB", "Final Project"), Video("secure_url", "public_id"), "English", "Web Development", 100, 8, 10000, 10, Image("secure_url", "public_id"), 4.8, true, "12/12/2021", 100000)
    private val dumpCourse2 = Course("124", "Let's Learn Laravel: A Guided Path For Beginners", "Brad Schiff", 1000, "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", 199.99, arrayListOf("Introduction", "HTML", "CSS", "Javascript", "Node", "React", "MongoDB", "Final Project"), Video("secure_url", "public_id"), "English", "Web Development", 100, 8, 10000, 10, Image("secure_url", "public_id"), 4.8, true, "12/12/2021", 100000)
    private val dumpCourse3 = Course("125", "MySQL For Beginners: Real Database Experience Real Fast", "Brad Schiff", 1000, "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", "Become a full-stack web developer with just one course. HTML, CSS, Javascript, Node, React, MongoDB and more!", 199.99, arrayListOf("Introduction", "HTML", "CSS", "Javascript", "Node", "React", "MongoDB", "Final Project"), Video("secure_url", "public_id"), "English", "Web Development", 100, 8, 10000, 10, Image("secure_url", "public_id"), 4.7, true, "12/12/2021", 100000)
    private val dumpCourseList = arrayListOf(dumpCourse1, dumpCourse2, dumpCourse3)
    private var instructorCourseListAdapter = InstructorCourseListAdapter(dumpCourseList)
    private lateinit var instructorCoursesOverview: TextView
    private lateinit var instructorCoursesView: RecyclerView
    private lateinit var seeAllBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_profile)

        backBtn = findViewById(R.id.backBtn5)
        instructorName = findViewById(R.id.instructorName)
        instructorHeadline = findViewById(R.id.instructorHeadline)
        instructorTotalStudents = findViewById(R.id.instructorTotalStudents)
        instructorTotalReviews = findViewById(R.id.instructorTotalReviews)
        instructorBio = findViewById(R.id.instructorBio)
        showMoreBtn = findViewById(R.id.showMoreBtn)
        instructorCoursesOverview = findViewById(R.id.instructorCoursesOverview)
        instructorCoursesView = findViewById(R.id.instructorCoursesView)
        seeAllBtn = findViewById(R.id.seeAllBtn)

        instructorName.text = "${dumpInst.firstName} ${dumpInst.lastName}"
        instructorHeadline.text = dumpInst.headline
        instructorTotalStudents.text = dumpInst.totalStudents.formatThousands()
        instructorTotalReviews.text = dumpInst.totalReviews.formatThousands()
        instructorBio.text = dumpInst.bio

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

        //TODO: get the number of courses taught by the instructor from db
        instructorCoursesOverview.text = "My course (9)"

        instructorCourseListAdapter = InstructorCourseListAdapter(dumpCourseList)
        instructorCoursesView.adapter = instructorCourseListAdapter
        instructorCoursesView.layoutManager = LinearLayoutManager(this)

        seeAllBtn.setOnClickListener {
            //TODO: make event to see all courses taught by the instructor
        }

        backBtn.setOnClickListener {
            //TODO: make event to go back to previous activity
        }
    }
}