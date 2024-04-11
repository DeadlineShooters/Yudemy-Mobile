package com.deadlineshooters.yudemy.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.adapters.CourseListAdapter2
import com.deadlineshooters.yudemy.databinding.ActivityEnrolledBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel

class EnrolledActivity : AppCompatActivity() {
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var binding: ActivityEnrolledBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolledBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        courseViewModel.courses.observe(this, Observer { courses ->
            val clonedCourses = List(1) { courses[0] }
            val courseListAdapter = CourseListAdapter2(this, clonedCourses)
            binding.courseList.layoutManager = LinearLayoutManager(this)
            binding.courseList.adapter = courseListAdapter
        })
    }
}
