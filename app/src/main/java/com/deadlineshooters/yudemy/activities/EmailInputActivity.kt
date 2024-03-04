package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class EmailInputActivity : AppCompatActivity() {
    private var nextBtn: Button? = null
    private var forgotHref: TextView? = null
    private var emailInput: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_input)

        nextBtn = findViewById(R.id.nextBtn)
        forgotHref = findViewById(R.id.forgotHref)
        emailInput = findViewById(R.id.emailInput)

        nextBtn?.setOnClickListener {
            val intent = Intent(this, PasswordInputActivity::class.java)
            intent.putExtra("email", emailInput?.text.toString())
            startActivity(intent);
            //TODO: check if email is valid, button change background to black and set button to clickable
        }

        forgotHref?.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent);
        }

    }
}