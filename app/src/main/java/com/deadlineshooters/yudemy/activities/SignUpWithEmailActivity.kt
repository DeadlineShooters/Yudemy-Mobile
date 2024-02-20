package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivitySignUpWithEmailBinding

class SignUpWithEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpWithEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpWithEmailBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_up_with_email)
        setupActionBar() // on back button click will navigate to the previous screen
    }

    /**
     * replace default ActionBar with the < button
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpWithEmailActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }

        binding.toolbarSignUpWithEmailActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
