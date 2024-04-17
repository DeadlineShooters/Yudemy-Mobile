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
import com.google.firebase.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class SignUpActivity : BaseActivity() {
    private var signinHref: TextView? = null
    private var signUpEmailBtn: Button? = null
    private var signUpGoogleBtn: Button? = null
    private var signUpFbBtn: Button? = null
    private val RC_SIGN_UP = 9001
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
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_UP)
            googleSignInClient.signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_UP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GoogleSignIn", account.email.toString())
                AuthenticationRepository().signUpWithGoogle(account) {uid ->
                    if (uid != null) {
                        val newUser = User("",account.displayName.toString(), Image("https://res.cloudinary.com/dbgaeu07x/image/upload/v1712737767/Yudemy/spmaesw65l7iyk32xymu.jpg","Yudemy/spmaesw65l7iyk32xymu") ,arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), false, "", null)
                        UserRepository().addUser(newUser)
                        val intent = Intent(this, StudentMainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "This email has been register already", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "signUpResult:failed code=" + e.statusCode)
                Toast.makeText(this, "Google sign up failed: " + e.statusCode, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
