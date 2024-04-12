package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class LectureRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lecturesCollection = mFireStore.collection(Constants.LECTURES)


    fun getLecturesBySectionId(sectionId: String): Task<List<Lecture>> {
        val task = mFireStore.collection("lectures")
            .whereEqualTo("sectionId", sectionId)
            .get()

        return task.continueWith { task ->
            if (task.isSuccessful) {
                val result = task.result
                val lectures = result?.map { document ->
                    val lecture = document.toObject(Lecture::class.java)
                    lecture._id = document.id
                    lecture
                } ?: emptyList()
                lectures
            } else {
                emptyList()
            }
        }
    }
}