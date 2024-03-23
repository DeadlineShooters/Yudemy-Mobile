package com.deadlineshooters.yudemy.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.utils.Constants.WEB_CLIENT_ID
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : BaseActivity() {
    private var signinHref: TextView? = null
    private var signUpEmailBtn: Button? = null
    private var signUpGoogleBtn: Button? = null
    private var signUpFbBtn: Button? = null
    private val RC_SIGN_IN = 9001
    private val auth = FirebaseAuth.getInstance()

    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signinHref = findViewById(R.id.signinHref)
        signUpEmailBtn = findViewById(R.id.signUpEmailBtn)
        signUpGoogleBtn = findViewById(R.id.signUpGoogleBtn)
        signUpFbBtn = findViewById(R.id.signUpFbBtn)

        signinHref!!.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        signUpEmailBtn!!.setOnClickListener {
            val intent = Intent(this, SignUpWithEmailActivity::class.java)
            startActivity(intent)
        }



        signUpGoogleBtn!!.setOnClickListener {
            signIn()
        }
    }


    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        Log.d("SignInActivity", signInIntent.action.toString())
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, StudentMainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}