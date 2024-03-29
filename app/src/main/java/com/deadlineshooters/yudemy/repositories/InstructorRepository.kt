package com.deadlineshooters.yudemy.repositories

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    fun getInstructor(): LiveData<List<Instructor>> {
        val instructorsLiveData = MutableLiveData<List<Instructor>>()

        lecturersCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val instructors = snapshot.documents.mapNotNull { it.toObject(Instructor::class.java) }
                instructorsLiveData.value = instructors
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }

        return instructorsLiveData
    }
}