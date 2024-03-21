package com.deadlineshooters.yudemy.repositories

import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.activities.SignUpActivity
import com.deadlineshooters.yudemy.models.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun getUserData() : User{
        // for testing
        return User("000", "Test")
    }
    fun addUser(user: User) {
        mFireStore.collection("users")
            .document(user.id)
            .set(user)
    }
}