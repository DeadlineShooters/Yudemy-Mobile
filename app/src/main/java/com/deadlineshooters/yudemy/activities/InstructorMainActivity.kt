package com.deadlineshooters.yudemy.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityInstructorMainBinding
import com.deadlineshooters.yudemy.databinding.ActivityMainBinding
import com.deadlineshooters.yudemy.fragments.AccountFragment
import com.deadlineshooters.yudemy.fragments.AnalyticsFragment
import com.deadlineshooters.yudemy.fragments.CoursesFragment
import com.deadlineshooters.yudemy.fragments.FeaturedFragment
import com.deadlineshooters.yudemy.fragments.MyLearningFragment
import com.deadlineshooters.yudemy.fragments.QAFragment
import com.deadlineshooters.yudemy.fragments.ReviewsFragment
import com.deadlineshooters.yudemy.fragments.SearchFragment
import com.deadlineshooters.yudemy.fragments.WishlistFragment

class InstructorMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInstructorMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructorMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(CoursesFragment())  // show Courses fragment firstly

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.courses -> {
                    replaceFragment(CoursesFragment())
                }

                R.id.qna -> {
                    replaceFragment(QAFragment())
                }

                R.id.analytics -> {
                    replaceFragment(AnalyticsFragment())
                }

                R.id.reviews -> {
                    replaceFragment(ReviewsFragment())
                }

                else -> {

                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}