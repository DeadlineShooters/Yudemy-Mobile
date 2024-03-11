package com.deadlineshooters.yudemy.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityMainBinding
import com.deadlineshooters.yudemy.fragments.AccountFragment
import com.deadlineshooters.yudemy.fragments.FeaturedFragment
import com.deadlineshooters.yudemy.fragments.MyLearningFragment
import com.deadlineshooters.yudemy.fragments.SearchFragment
import com.deadlineshooters.yudemy.fragments.WishlistFragment
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {
    val db = Firebase.firestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        replaceFragment(FeaturedFragment())  // show Featured fragment firstly

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
                    replaceFragment(AccountFragment())
                }

                else -> {

                }
            }
            true
        }

    }
    fun getToolbarTitle(): TextView? {
        return binding.toolbarTitle
    }



    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}