package com.deadlineshooters.yudemy.viewmodels

import com.deadlineshooters.yudemy.activities.SignUpActivity
import com.deadlineshooters.yudemy.models.User
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
//        mFireStore.collection(Constants.USERS)...
    }
}