package com.deadlineshooters.yudemy.repositories

import android.content.Context
import com.google.firebase.auth.EmailAuthProvider
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.deadlineshooters.yudemy.activities.StudentMainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.security.auth.callback.Callback

class AuthenticationRepository {
    private val auth = FirebaseAuth.getInstance()

    fun createAccount(context: Context, email: String, password: String, callback: (String?) -> Unit)  {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Please verify a verify email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                                waitForEmailVerification(user) { isVerified ->
                                    if (isVerified) {
                                        val uId = user.uid
                                        callback(uId)
                                    }
                                }
                            }
                        }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        Log.d("auth error", "User already exists with this email")
                        callback("User already exists with this email")
                    } else {
                        // Xử lý các loại lỗi khác
                        Log.d("auth error", exception?.message ?: "Unknown error")
                        callback(null)
                    }
                }
            }
    }


    fun logInWithEmail(email: String, password: String, callback: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uId = user?.uid
                    callback(uId)
                } else {
                    callback(null)
                }
            }
    }

    fun signOut(callback: (Boolean?) -> Unit){
        auth.signOut()
        if(auth.currentUser == null){
            callback(true)
        } else {
            callback(false)
        }
    }

    fun signUpWithGoogle(account: GoogleSignInAccount, callback: (String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uId = user?.uid
                    UserRepository().getUserById(uId!!){it ->
                        if(it == null){
                            callback(uId)
                        } else{
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, callback: (String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uId = user?.uid
                    UserRepository().getUserById(uId!!){it ->
                        if(it == null){
                            Log.d("User", "User not found")
                            closeAccount(){success, userId ->
                                if(success){
                                    callback(null)
                                }
                            }
                        } else{
                            callback(uId)
                        }
                    }
                } else {
                    callback(null)
                }
            }
    }

    fun changePassword(newPassword: String, callback: (Boolean?) -> Unit){
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun checkPassword(password: String, callback: (Boolean?) -> Unit){
        val user = auth.currentUser

        val credential = user?.email?.let { EmailAuthProvider.getCredential(it, password) }
        user?.reauthenticate(credential!!)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun closeAccount(callback: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        auth.currentUser?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, userId)
            } else {
                callback(false, null)
            }
        }
    }

    fun resetPassword(email: String, callback: (Boolean?) -> Unit){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    callback(true)
                }
            }
    }

    fun waitForEmailVerification(user: FirebaseUser, callback: (Boolean) -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                user.reload().addOnCompleteListener {
                    if (user.isEmailVerified) {
                        callback(true)
                    } else {
                        // Repeat this check every 5 seconds
                        handler.postDelayed(this, 5000)
                    }
                }
            }
        }
        // Start the loop
        handler.post(runnable)
    }
}
