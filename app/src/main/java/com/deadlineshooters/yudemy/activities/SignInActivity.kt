package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R

class SignInActivity : AppCompatActivity() {
    private var signupHref: TextView? = null
    private var closeBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signupHref = findViewById(R.id.signupHref)
        closeBtn = findViewById(R.id.closeBtn)

        signupHref?.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent);
        }


    }
}