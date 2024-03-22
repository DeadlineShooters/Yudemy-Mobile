package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.models.Lecture
import com.google.firebase.firestore.FirebaseFirestore

class LectureRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lecturesCollection = mFireStore.collection("lectures")

    fun getLecturesBySection(sectionId: String): ArrayList<Lecture>? {
        var list: ArrayList<Lecture>? = arrayListOf()

        lecturesCollection
            .whereEqualTo("sectionId", sectionId)
            .orderBy("index")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val lecture = document.toObject(Lecture::class.java)
                    list!!.add(lecture)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                list = null
            }
        return list
    }
}