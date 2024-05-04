package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.deadlineshooters.yudemy.repositories.UserRepository

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
    private var signInHref: TextView? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var name: EditText? = null
    private var backBtn4: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_with_email)

        signUpBtn = findViewById(R.id.signUpBtn)
        signInHref = findViewById(R.id.signInHref)
        name = findViewById(R.id.nameInput)
        email = findViewById(R.id.mailInput)
        password = findViewById(R.id.passInput)
        backBtn4 = findViewById(R.id.backBtn4)

        signInHref!!.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        backBtn4!!.setOnClickListener{
            finish()
        }

        signUpBtn!!.setOnClickListener{
            AuthenticationRepository().createAccount(this, email!!.text.toString(), password!!.text.toString()){uid ->
                if(uid == "User already exists with this email"){
                    Toast.makeText(this, "${email!!.text} is already in used", Toast.LENGTH_SHORT).show()
                }
                else {
                    if(uid != null){
                        val newUser = User("",name!!.text.toString(), Image("https://res.cloudinary.com/dbgaeu07x/image/upload/v1712737767/Yudemy/spmaesw65l7iyk32xymu.jpg","Yudemy/spmaesw65l7iyk32xymu") ,arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), false, "", null)
                        UserRepository().addUser(newUser)
                        val intent = Intent(this, StudentMainActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
