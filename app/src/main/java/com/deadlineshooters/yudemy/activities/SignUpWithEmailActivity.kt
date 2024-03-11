package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivitySignUpWithEmailBinding

class SignUpWithEmailActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySignUpWithEmailBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignUpWithEmailBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_sign_up_with_email)
//        setupActionBar() // on back button click will navigate to the previous screen
//    }
//
//    /**
//     * replace default ActionBar with the < button
//     */
//    private fun setupActionBar() {
//        setSupportActionBar(binding.toolbarSignUpWithEmailActivity)
//
//        val actionBar = supportActionBar
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
//        }
//
//        binding.toolbarSignUpWithEmailActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    private var signUpBtn: Button? = null
    private var signinHref: TextView? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var name: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_with_email)

        signUpBtn = findViewById(R.id.signUpBtn)
        signinHref = findViewById(R.id.signinHref)
        name = findViewById(R.id.nameInput)
        email = findViewById(R.id.mailInput)
        password = findViewById(R.id.passInput)

        signinHref!!.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
