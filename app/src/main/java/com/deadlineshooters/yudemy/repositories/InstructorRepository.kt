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
    private val instructorCollection = mFireStore.collection("users")
    private val courseCollection = mFireStore.collection("courses")

    fun addInstructor(instructor: User) {
        val documentReference = instructorCollection.document()
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

        instructorCollection.addSnapshotListener { snapshot, e ->
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

    fun getInstructorByCourseId(courseId: String, callbacks: (User) -> Unit){
        courseCollection.document(courseId).get().addOnSuccessListener { document ->
            if (document != null) {
                val instructorId = document.getString("instructor")
                if (instructorId != null) {
                    instructorCollection.document(instructorId).get().addOnSuccessListener { document ->
                        callbacks(document.toObject(User::class.java)!!)
                    }
                }
            }
        }
    }

    fun getInstructorById(instructorId: String, callbacks: (User) -> Unit){
        instructorCollection.document(instructorId).get().addOnSuccessListener { document ->
            callbacks(document.toObject(User::class.java)!!)
        }
    }

    fun modifyInstructorProfile(instructorId: String, fullName: String, headLine: String, bio: String, callbacks: (User) -> Unit){
        instructorCollection.document(instructorId).update(
            "fullName" , fullName,
            "instructor.headline", headLine,
            "instructor.bio", bio
        ).addOnSuccessListener {
            getInstructorById(instructorId){
                callbacks(it)
            }
        }.addOnFailureListener {
            Log.w(ContentValues.TAG, "Error updating document", it)
        }
    }

    fun updateInstructorImage(instructorId: String, pubicId: String, secureUrl: String, callbacks: (User) -> Unit){
        instructorCollection.document(instructorId).update(
            "instructor.image.public_id", pubicId,
            "instructor.image.secure_url", secureUrl
        ).addOnSuccessListener {
            getInstructorById(instructorId){
                callbacks(it)
            }
        }.addOnFailureListener {
            Log.w(ContentValues.TAG, "Error updating document", it)
        }
    }

    fun getInstructorNameById(instructorId: String, callback: (String?) -> Unit) {
        instructorCollection.document(instructorId)
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