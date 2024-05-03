package com.deadlineshooters.yudemy.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.DetailSectionAdapter
import com.deadlineshooters.yudemy.adapters.FeedbackAdapter
import com.deadlineshooters.yudemy.adapters.StudentFeedbackAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCourseDetailBinding
import com.deadlineshooters.yudemy.databinding.ActivityStudentFeedbackBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback

class StudentFeedbackActivity : AppCompatActivity() {
    private lateinit var feedbackAdapter: StudentFeedbackAdapter
    private lateinit var binding: ActivityStudentFeedbackBinding
    private lateinit var feedbackList : ArrayList<CourseFeedback>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        feedbackList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("feedbackList", CourseFeedback::class.java)!!
        } else {
            intent.getParcelableArrayListExtra("feedbackList")!!
        }
        feedbackAdapter = StudentFeedbackAdapter(feedbackList)

        binding.rvFeedback.adapter = feedbackAdapter
        binding.rvFeedback.layoutManager = LinearLayoutManager(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

}