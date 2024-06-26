package com.deadlineshooters.yudemy.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityMainBinding
import com.deadlineshooters.yudemy.fragments.AccountFragment
import com.deadlineshooters.yudemy.fragments.FeaturedFragment
import com.deadlineshooters.yudemy.fragments.MyLearningFragment
import com.deadlineshooters.yudemy.fragments.SearchFragment
import com.deadlineshooters.yudemy.fragments.WishlistFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentMainActivity : BaseActivity() {
    val db = Firebase.firestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val isUpdateProgressPassed = intent.getBooleanExtra("isUpdateProgressPassed", false)
        if (isUpdateProgressPassed) {
            // If isUpdateProgress was passed, replace fragment with MyLearningFragment
            val isUpdateProgress = intent.getBooleanExtra("isUpdateProgress", false)
            replaceFragment(MyLearningFragment.newInstance(isUpdateProgress))
            binding.bottomNavigationView.selectedItemId = R.id.my_learning
        } else {
            // If isUpdateProgress was not passed, show FeaturedFragment
            replaceFragment(FeaturedFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.featured -> {
                    replaceFragment(FeaturedFragment())
                }

                R.id.search -> {
                    replaceFragment(SearchFragment())
                }

                R.id.my_learning -> {
                    replaceFragment(MyLearningFragment())
                }

                R.id.wishlist -> {
                    replaceFragment(WishlistFragment())
                }

                R.id.account -> {
                    replaceFragment(AccountFragment.newInstance(false, this.getCurrentUserID()))
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
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}