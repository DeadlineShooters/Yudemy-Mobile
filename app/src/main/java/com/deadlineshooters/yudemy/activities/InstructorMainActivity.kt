package com.deadlineshooters.yudemy.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityInstructorMainBinding
import com.deadlineshooters.yudemy.fragments.AccountFragment
import com.deadlineshooters.yudemy.fragments.AnalyticsFragment
import com.deadlineshooters.yudemy.fragments.CourseDashboardFragment
import com.deadlineshooters.yudemy.fragments.FeedbackFragment
import com.deadlineshooters.yudemy.fragments.InstructorQAFragment

class InstructorMainActivity : BaseActivity() {
    private lateinit var binding: ActivityInstructorMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructorMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(CourseDashboardFragment())  // show Courses fragment firstly

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.courses -> {
                    replaceFragment(CourseDashboardFragment())
                }

                R.id.qna -> {
                    replaceFragment(InstructorQAFragment())
                }

                R.id.analytics -> {
                    replaceFragment(AnalyticsFragment())
                }

                R.id.reviews -> {
                    replaceFragment(FeedbackFragment())
                }

                R.id.account -> {
                    replaceFragment(AccountFragment.newInstance(true, this.getCurrentUserID()))
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
        fragmentTransaction.replace(R.id.frameLayoutInstructor, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}