package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CourseProgressRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val courseProgressCollection = mFireStore.collection("course_progress")

    fun getCourseProgressByCourse(courseId: String, callback: (Number) -> Unit) {
        courseProgressCollection
            .whereEqualTo("userId", mAuth.currentUser!!.uid)
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    callback(document.data["percentCompleted"] as Number)
                    break
                }
            }
    }

    fun updateCourseProgress(courseId: String, percentCompleted: Number) {
        Log.d("LectureLearningFragment", "updateCourseProgress: $courseId, $percentCompleted")
        courseProgressCollection
            .whereEqualTo("userId", mAuth.currentUser!!.uid)
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    courseProgressCollection.document(document.id).update("percentCompleted", percentCompleted)
                }
            }
            .addOnFailureListener {
                Log.w("Firestore", "Error getting documents: ", it)
            }
    }
}