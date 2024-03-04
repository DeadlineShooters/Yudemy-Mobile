package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class ResetPasswordActivity : AppCompatActivity() {
    private var emailReset: TextView? = null
    private var submitBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        emailReset = findViewById(R.id.emailReset)
        submitBtn = findViewById(R.id.submitBtn)

        submitBtn!!.setOnClickListener {
            val intent = Intent(this, ConfirmResetActivity::class.java)
            intent.putExtra("email", emailReset?.text.toString())
            startActivity(intent);
        }

    }
}