package com.deadlineshooters.yudemy.repositories

import com.google.firebase.auth.EmailAuthProvider
import android.content.Intent
import android.widget.Toast
import com.deadlineshooters.yudemy.activities.StudentMainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.security.auth.callback.Callback

class AuthenticationRepository {
    private val auth = FirebaseAuth.getInstance()

    fun createAccount(email: String, password: String, callback: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
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

//    fun firebaseAuthWithGoogle(idToken: String, callback: (String?) -> Unit) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener() { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    val uId = user?.uid
//                    callback(uId)
//                } else {
//                    callback(null)
//                }
//            }
//    }

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
}
