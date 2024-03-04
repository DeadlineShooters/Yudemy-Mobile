package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class ConfirmResetActivity : AppCompatActivity() {
    private var emailConfirm: TextView? = null
    private var signInBtn: Button? = null
    private var backBtn3: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_password)

        emailConfirm = findViewById(R.id.emailConfirm)
        signInBtn = findViewById(R.id.signInBtn)
        backBtn3 = findViewById(R.id.backBtn3)

        emailConfirm!!.text = intent.getStringExtra("email")

        signInBtn!!.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java);
            startActivity(intent);
        }

        backBtn3!!.setOnClickListener {
            finish()
        }
    }
}