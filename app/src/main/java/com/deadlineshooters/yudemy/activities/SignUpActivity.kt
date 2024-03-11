package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class SignUpActivity : BaseActivity() {
    private var signinHref: TextView? = null
    private var signUpEmailBtn: Button? = null
    private var signUpGoogleBtn: Button? = null
    private var signUpFbBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signinHref = findViewById(R.id.signinHref)
        signUpEmailBtn = findViewById(R.id.signUpEmailBtn)
        signUpGoogleBtn = findViewById(R.id.signUpGoogleBtn)
        signUpFbBtn = findViewById(R.id.signUpFbBtn)

        signinHref!!.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        signUpEmailBtn!!.setOnClickListener{
            val intent = Intent(this, SignUpWithEmailActivity::class.java)
            startActivity(intent)
        }
    }
}