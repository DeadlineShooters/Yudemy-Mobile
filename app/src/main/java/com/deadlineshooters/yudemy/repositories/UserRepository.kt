package com.deadlineshooters.yudemy.repositories

import android.content.ComponentCallbacks
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.activities.SignUpActivity
import com.deadlineshooters.yudemy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    fun getUserData() : User{
        // for testing
        return User("Test")
    }
    fun addUser(user: User) {
        mFireStore.collection("users")
            .document(mAuth.currentUser!!.uid)
            .set(user)
    }

    fun getCurUser(callbacks: (User) -> Unit){
        mFireStore.collection("users")
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    callbacks(document.toObject(User::class.java)!!)
                }
            }
    }

    fun getUserById(userId: String, callbacks: (User) -> Unit){
        mFireStore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    callbacks(document.toObject(User::class.java)!!)
                }
            }
    }
}