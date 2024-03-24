package com.deadlineshooters.yudemy.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.activities.SignUpActivity
import com.deadlineshooters.yudemy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val userCollection = mFireStore.collection("users")
    private val mAuth = FirebaseAuth.getInstance()
    fun getUserData(): User {
        // for testing
        return User("Test")
    }

    fun addUser(user: User) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .set(user)
    }

    fun getWishlistID(callback: (List<String>) -> Unit) {
        val uid = mAuth.currentUser?.uid
        userCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as List<String>
                    callback(wishlist) // Pass the wishlist to the callback function
                } else {
                    Log.d("Firestore", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }


}