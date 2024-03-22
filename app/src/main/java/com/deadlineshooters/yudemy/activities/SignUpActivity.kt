package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.deadlineshooters.yudemy.utils.Constants.WEB_CLIENT_ID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : BaseActivity() {
    private var signinHref: TextView? = null
    private var signUpEmailBtn: Button? = null
    private var signUpGoogleBtn: Button? = null
    private var signUpFbBtn: Button? = null
    private val googleSignInRC = 9001
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signinHref = findViewById(R.id.signinHref)
        signUpEmailBtn = findViewById(R.id.signUpEmailBtn)
        signUpGoogleBtn = findViewById(R.id.signUpGoogleBtn)
        signUpFbBtn = findViewById(R.id.signUpFbBtn)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signinHref!!.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        signUpEmailBtn!!.setOnClickListener {
            val intent = Intent(this, SignUpWithEmailActivity::class.java)
            startActivity(intent)
        }

        signUpGoogleBtn!!.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, googleSignInRC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            googleSignInRC -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.result
                    AuthenticationRepository().firebaseAuthWithGoogle(account!!.idToken!!){
                        if(it != null){
                            startActivity(Intent(this, StudentMainActivity::class.java))
                        } else {
                            Toast.makeText(this, "Failed to sign in with Google", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}