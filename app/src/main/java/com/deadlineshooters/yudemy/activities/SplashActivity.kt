package com.deadlineshooters.yudemy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.deadlineshooters.yudemy.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        // Adding the handler to after the a task after some delay.
        Handler().postDelayed({

            // Here if the user is signed in once and not signed out again from the app. So next time while coming into the app
            // we will redirect him to MainScreen or else to the Intro Screen as it was before.

//            // Get the current user id
//            val currentUserID = FirestoreClass().getCurrentUserID()
//            // Start the Intro Activity

//            if (currentUserID.isNotEmpty()) {
//                // Start the Main Activity
//                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            } else {
                // Start the Intro Activity
                startActivity(Intent(this@SplashActivity, StudentMainActivity::class.java))
//            }
            finish() // Call this when your activity is done and should be closed.
        }, 2500) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
    }
}