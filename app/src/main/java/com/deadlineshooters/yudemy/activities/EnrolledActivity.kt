package com.deadlineshooters.yudemy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.hits.connectHitsView
import com.algolia.search.helper.deserialize
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.CourseSearchAdapter
import com.deadlineshooters.yudemy.databinding.ActivityEnrolledBinding
import com.deadlineshooters.yudemy.models.AlgoliaCourse
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.QuerySuggestionViewModel

class EnrolledActivity : AppCompatActivity() {
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var binding: ActivityEnrolledBinding
    private lateinit var course: Course
    private val viewModel by viewModels<QuerySuggestionViewModel>()
    private val connection = ConnectionHandler()
    val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolledBinding.inflate(layoutInflater)
        setContentView(binding.root)
        course = intent.getParcelableExtra<Course>("course") ?: Course()

        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.courseName.text = course.name
        userRepository.getUserById(course.instructor) { instructor ->
            binding.instructorName.text = instructor!!.fullName
        }
        binding.gotoCourse.setOnClickListener {
            val intent = Intent(this@EnrolledActivity, CourseLearningActivity::class.java)
            intent.putExtra("course", course)
            startActivity(intent)
        }

        Glide.with(this)
            .load(course.thumbnail.secure_url)
            .into(binding.thumbnail);

        val courseListAdapter = CourseSearchAdapter()
        binding.courseList.layoutManager = LinearLayoutManager(this)
        binding.courseList.adapter = courseListAdapter
        connection += viewModel.courseSearcher.connectHitsView(courseListAdapter) {
            it.hits.deserialize(AlgoliaCourse.serializer()).filter { algoliaCourse ->
                algoliaCourse.objectID.toString() != course.id
            }
        }

        viewModel.courseSearcher.setQuery(course.category)
        viewModel.courseSearcher.searchAsync()

        viewModel.courseSearcher.response.subscribe { response ->
            val courses = response?.hits?.deserialize(AlgoliaCourse.serializer())
            if (courses != null) {
                if (courses.size == 1 && courses[0].objectID.toString() == course.id) {
                    binding.courseListTitle.visibility = GONE
                } else {
                    binding.courseListTitle.visibility = VISIBLE
                }
            }
        }
    }
}
