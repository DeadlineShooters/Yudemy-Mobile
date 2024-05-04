package com.deadlineshooters.yudemy.repositories

import android.content.ContentValues
import android.util.Log
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.UserLecture
import com.deadlineshooters.yudemy.models.Video
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserLectureRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val userLectureCollection = mFireStore.collection("user_lectures")
    private val mAuth = FirebaseAuth.getInstance()

    fun checkLectureFinished(userId: String, lectureId: String, callback: (Boolean) -> Unit) {
        var isFinished = false
        userLectureCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("lectureId", lectureId)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("Firestore", "addOnSuccessListener: ${documents.documents}")
                for (document in documents) {
                    isFinished = document.data["finished"] as Boolean
                }
                Log.d("LectureLearningFragment", "repo checkFinished: $isFinished")
                callback(isFinished)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(isFinished)
            }
    }

    fun getUserLecturesBySection(userId: String, sectionId: String, callback: (ArrayList<Map<Lecture, Boolean>>) -> Unit) {
        val list: ArrayList<Map<Lecture, Boolean>> = arrayListOf()

        mFireStore.collection("lectures")
            .whereEqualTo("sectionId", sectionId)
            .orderBy("index")
            .get()
            .addOnSuccessListener { documents ->
                val tasks = documents.map { document ->
                    val task = TaskCompletionSource<Map<Lecture, Boolean>>()
                    val lecture = document.toObject(Lecture::class.java)
                    val contentMap = document.get("content") as Map<String, Any>
                    val video = Video(
                        secure_url = contentMap["secure_url"] as String,
                        public_id = contentMap["public_id"] as String,
                        resource_type = contentMap["resource_type"] as String,
                        duration = contentMap["duration"] as Double
                    )
                    lecture.content = video

                    checkLectureFinished(userId, lecture._id) { isFinished ->
                        task.setResult(mapOf(lecture to isFinished))
                    }
                    task.task
                }
                Tasks.whenAllSuccess<Map<Lecture, Boolean>>(tasks)
                    .addOnSuccessListener { lectures ->
                        Log.d("LectureLearningFragment", "repo getUserLecturesBySection: $lectures")
                        callback(lectures as ArrayList<Map<Lecture, Boolean>>)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(list)
            }
    }

    fun getUserLecturesByCourse(courseId: String, callback: (ArrayList<ArrayList<Map<Lecture, Boolean>>>) -> Unit) {
        val userId = mAuth.currentUser!!.uid

        val list: ArrayList<ArrayList<Map<Lecture, Boolean>>> = arrayListOf()

        mFireStore.collection("courses").document(courseId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.data?.get("sectionList")
                    val idList = if (data != null) {
                        data as ArrayList<String>
                    } else {
                        // data is null
                        ArrayList<String>()
                    }
                    val tasks = idList.map { id ->
                        val task = TaskCompletionSource<ArrayList<Map<Lecture, Boolean>>>()
                        getUserLecturesBySection(userId, id) { lectures ->
                            task.setResult(lectures)
                        }
                        task.task
                    }
                    Tasks.whenAllSuccess<ArrayList<Lecture>>(tasks)
                        .addOnSuccessListener { lectures ->
                            Log.d("LectureLearningFragment", "repo getUserLecturesByCourse: $lectures")
                            val data = lectures
                            val lectures = if (data is ArrayList<*>) {
                                data as ArrayList<ArrayList<Map<Lecture, Boolean>>>
                            } else {
                                // data is null or an empty list
                                ArrayList<ArrayList<Map<Lecture, Boolean>>>()
                            }

                            callback(lectures)

//                            callback(lectures as ArrayList<ArrayList<Map<Lecture, Boolean>>>)
                        }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(list)
            }
    }

    fun markLecture(lectureId: String, isCompleted: Boolean) {
        userLectureCollection
            .whereEqualTo("userId", mAuth.currentUser!!.uid)
            .whereEqualTo("lectureId", lectureId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.update("finished", isCompleted)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }

    fun newUserLecture(userLecture: UserLecture, callback: (Boolean) -> Unit) {
        userLectureCollection
            .add(userLecture)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "add failed with ", exception)
                callback(false)
            }
    }
}