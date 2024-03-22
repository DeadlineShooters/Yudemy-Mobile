package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.UserLecture
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class UserLectureRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val userLectureCollection = mFireStore.collection("userLectures")

//    fun getUserLectureBySection(sectionId: String, userId: String): List<UserLecture>? {
//        var list: List<UserLecture>? = listOf()
//
//        userLectureCollection
//            .whereEqualTo("userId", userId)
//            .orderBy("index")
//            .get()
//            .addOnSuccessListener { documents ->
//                list = documents.map { it.toObject<UserLecture>() }
//            }
//            .addOnFailureListener { exception ->
//                exception.printStackTrace()
//                list = null
//            }
//        return list
//    }
}