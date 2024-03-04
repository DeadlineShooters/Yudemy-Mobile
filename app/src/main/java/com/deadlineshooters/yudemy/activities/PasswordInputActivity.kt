package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class PasswordInputActivity : AppCompatActivity() {
    private var email: TextView? = null
    private var forgotHref: TextView? = null
    private var signinBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_input)

        email = findViewById(R.id.email)
        forgotHref = findViewById(R.id.forgotHref)
        email?.text = intent.getStringExtra("email")
        signinBtn = findViewById(R.id.signinBtn)

        forgotHref!!.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent);
        }

        signinBtn!!.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
            //TODO: Check password from database
        }

    }
}