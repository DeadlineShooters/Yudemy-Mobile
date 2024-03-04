package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class PasswordInputActivity : AppCompatActivity() {
    private var email: TextView? = null
    private var forgotHref: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_input)

        email = findViewById(R.id.email)
        forgotHref = findViewById(R.id.forgotHref)
        email?.text = intent.getStringExtra("email")
        
    }
}