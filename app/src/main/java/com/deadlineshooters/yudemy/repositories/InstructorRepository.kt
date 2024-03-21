package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.User
import com.google.firebase.firestore.FirebaseFirestore

class InstructorRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lecturersCollection = mFireStore.collection("lecturers")

    fun addInstructor(instructor: User) {
        val documentReference = lecturersCollection.document()
        instructor.id = documentReference.id
        documentReference.set(instructor)
            .addOnSuccessListener {
                Log.d("Lecturer", "DocumentSnapshot successfully written!\n$instructor")
            }
            .addOnFailureListener {
                    e -> Log.w("Lecturer", "Error writing document", e)
            }
    }
}