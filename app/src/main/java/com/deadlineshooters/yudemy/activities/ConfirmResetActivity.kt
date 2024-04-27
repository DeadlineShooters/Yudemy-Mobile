package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository

class ConfirmResetActivity : AppCompatActivity() {
    private var emailConfirm: TextView? = null
    private var signInBtn: Button? = null
    private var backBtn3: Button? = null
    private lateinit var resendEmail: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_password)

        emailConfirm = findViewById(R.id.emailConfirm)
        signInBtn = findViewById(R.id.signInBtn)
        backBtn3 = findViewById(R.id.backBtn3)
        resendEmail = findViewById(R.id.sendConfirm)
        emailConfirm!!.text = intent.getStringExtra("email")

        resendEmail.setOnClickListener {
            AuthenticationRepository().resetPassword(emailConfirm?.text.toString()){it ->
                if(it == true){
                    Toast.makeText(this, "We have sent new reset password email for you", Toast.LENGTH_SHORT).show()
                }
            }
        }

        signInBtn!!.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java);
            startActivity(intent);
        }

        backBtn3!!.setOnClickListener {
            finish()
        }
    }
}