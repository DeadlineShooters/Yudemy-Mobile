package com.deadlineshooters.yudemy.activities

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityCourseRevenueAnalyticsBinding

class CourseRevenueAnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseRevenueAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCourseRevenueAnalyticsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_course_revenue_analytics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionBar()

        val dateOptions = arrayOf("Last 7 days", "Last 30 days", "Last 12 months", "All time")

// Create an ArrayAdapter using the array and a default spinner layout
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            dateOptions
        )

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the Spinner
        binding.dateRangeFilter.adapter = adapter
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarRevenue)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }

        binding.toolbarRevenue.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}