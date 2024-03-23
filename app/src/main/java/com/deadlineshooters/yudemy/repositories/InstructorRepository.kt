package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Instructor
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.models.Course
import android.content.ContentValues

class InstructorRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val instructorsCollection = mFireStore.collection("lecturers")

    fun addInstructor(instructor: Instructor) {
        val documentReference = instructorsCollection.document()
        instructor._id = documentReference.id
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

        instructorsCollection.addSnapshotListener { snapshot, e ->
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

    fun getInstructorNameById(instructorId: String, callback: (String?) -> Unit) {
        mFireStore.collection("users")
            .document(instructorId)
            .get()
            .addOnSuccessListener { document ->
                callback(document?.get("fullName") as String?)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(null)
            }
    }
}