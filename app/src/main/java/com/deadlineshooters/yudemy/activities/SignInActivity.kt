package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInActivity : BaseActivity() {
    private var signupHref: TextView? = null
    private var signInEmailBtn: Button? = null
    private lateinit var signInGoogleBtn: Button
    private val RC_SIGN_IN = 9002
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signupHref = findViewById(R.id.signupHref)
        signInEmailBtn = findViewById(R.id.signInEmailBtn)
        signInGoogleBtn = findViewById(R.id.signInGoogleBtn)

        signupHref?.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent);
        }

        signInEmailBtn?.setOnClickListener {
            val intent = Intent(this, EmailInputActivity::class.java)
            startActivity(intent);
        }

        signInGoogleBtn.setOnClickListener{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            googleSignInClient.signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                AuthenticationRepository().signInWithGoogle(account) { uid ->
                    if(uid == null){
                        Toast.makeText(this, "This email has not been register yet", Toast.LENGTH_SHORT).show()
                    }
                    if (uid != null) {
                        val intent = Intent(this, StudentMainActivity::class.java)
                        startActivity(intent)
                    }
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
                Toast.makeText(this, "Google sign in failed: " + e.statusCode, Toast.LENGTH_SHORT).show()
            }
        }
    }
}