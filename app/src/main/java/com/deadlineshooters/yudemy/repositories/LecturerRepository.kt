package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecturer
import com.google.firebase.firestore.FirebaseFirestore

class LecturerRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lecturersCollection = mFireStore.collection("lecturers")

    fun addLecturer(lecturer: Lecturer) {
        val documentReference = lecturersCollection.document()
        lecturer._id = documentReference.id
        documentReference.set(lecturer)
            .addOnSuccessListener {
                Log.d("Lecturer", "DocumentSnapshot successfully written!\n$lecturer")
            }
            .addOnFailureListener {
                    e -> Log.w("Lecturer", "Error writing document", e)
            }
    }
}