package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.deadlineshooters.yudemy.repositories.UserRepository
import org.w3c.dom.Text

class PasswordInputActivity : BaseActivity() {
    private var email: TextView? = null
    private var forgotHref: TextView? = null
    private var signinBtn: Button? = null
    private var backBtn1: Button? = null
    private var password: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_input)

        email = findViewById(R.id.email)
        forgotHref = findViewById(R.id.forgotHref)
        email?.text = intent.getStringExtra("email")
        signinBtn = findViewById(R.id.signinBtn)
        backBtn1 = findViewById(R.id.backBtn1)
        password = findViewById(R.id.passwordInput)

        forgotHref!!.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent);
        }

        signinBtn!!.setOnClickListener{
            AuthenticationRepository().logInWithEmail(email?.text.toString(), password!!.text.toString()) {uid ->
                if (uid != null) {
                    val intent = Intent(this, StudentMainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        backBtn1!!.setOnClickListener {
            finish()
        }

    }
}