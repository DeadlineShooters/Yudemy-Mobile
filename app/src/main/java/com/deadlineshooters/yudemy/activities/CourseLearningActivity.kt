package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.fragments.LectureLearningFragment
import com.deadlineshooters.yudemy.fragments.MoreLearningFragment
import com.deadlineshooters.yudemy.adapters.TabsAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CourseLearningActivity : AppCompatActivity() {
    private lateinit var courseLearningTabsAdapter: TabsAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_learning)

        viewPager = findViewById(R.id.mViewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val intent = intent
        val courseId = intent.getStringExtra("courseId")

        val fragments = listOf(LectureLearningFragment.newInstance(courseId), MoreLearningFragment.newInstance(courseId))
        courseLearningTabsAdapter = TabsAdapter(fragments, supportFragmentManager, lifecycle)
        viewPager.adapter = courseLearningTabsAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Lectures"
                1 -> "More"
                else -> "Lectures"
            }
        }.attach()
    }
}