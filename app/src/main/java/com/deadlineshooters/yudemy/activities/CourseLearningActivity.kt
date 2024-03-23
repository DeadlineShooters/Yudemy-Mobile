package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.fragments.LectureLearningFragment
import com.deadlineshooters.yudemy.fragments.MoreLearningFragment
import com.deadlineshooters.yudemy.adapters.TabsAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCourseLearningBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CourseLearningActivity : AppCompatActivity() {
    private lateinit var courseLearningTabsAdapter: TabsAdapter
    private lateinit var binding: ActivityCourseLearningBinding
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var instructorViewModel: InstructorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
//        val courseId = intent.getStringExtra("courseId")// TODO: Uncomment this line
        val courseId = "2tNxr8j5FosEueZrL3wH"

        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        courseViewModel.getLearningCourse(courseId!!)

        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]

        courseViewModel.learningCourse.observe(this, Observer{
            binding.courseLearningTitle.text = it?.name
            instructorViewModel.getLearningInstructorName(it?.instructor!!)

            instructorViewModel.learningInstructorName.observe(this, Observer {name ->
                binding.courseLearningIns.text = name
            })
        })

        val fragments = listOf(LectureLearningFragment.newInstance(courseId!!), MoreLearningFragment.newInstance(courseId))
        courseLearningTabsAdapter = TabsAdapter(fragments, supportFragmentManager, lifecycle)
        binding.mViewPager.adapter = courseLearningTabsAdapter

        TabLayoutMediator(binding.tabLayout, binding.mViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Lectures"
                1 -> "More"
                else -> "Lectures"
            }
        }.attach()
    }
}