package com.deadlineshooters.yudemy.repositories

import com.google.firebase.firestore.FirebaseFirestore

class LectureRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lecturesCollection = mFireStore.collection("lectures")
}