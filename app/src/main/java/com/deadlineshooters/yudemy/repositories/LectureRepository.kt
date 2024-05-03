package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
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

    fun getLectureById(lectureId: String, callback: (Lecture) -> Unit) {
        lecturesCollection.document(lectureId)
            .get()
            .addOnSuccessListener { result ->
                val lecture = result.toObject(Lecture::class.java)
                callback(lecture!!)
            }
            .addOnFailureListener { exception ->
                callback(Lecture())
            }
    }

    fun getLectureListByCourseId(courseId: String, callback: (ArrayList<Lecture>) -> Unit) {
        mFireStore.collection("courses")
            .document(courseId)
            .get()
            .addOnSuccessListener { result ->
                val sectionList: ArrayList<String> = result.data?.get("sectionList") as ArrayList<String>
                val lectureTasks = sectionList.map { section ->
                    val task = TaskCompletionSource<ArrayList<Lecture>>()
                    lecturesCollection.whereEqualTo("sectionId", section).get()
                        .addOnSuccessListener { lectureList ->
                            val lectureFetchTasks = lectureList.map { lecture ->
                                val lectureTask = TaskCompletionSource<Lecture>()
                                val lectureId = lecture.id
                                getLectureById(lectureId, callback = {
                                    lectureTask.setResult(it)
                                })
                                lectureTask.task
                            }
                            Tasks.whenAllSuccess<Lecture>(lectureFetchTasks).addOnSuccessListener { lectures ->
                                task.setResult(lectures as ArrayList<Lecture>)
                            }
                        }
                    task.task
                }
                Tasks.whenAllSuccess<ArrayList<Lecture>>(lectureTasks).addOnSuccessListener { lectures ->
                    callback(lectures.flatten() as ArrayList<Lecture>)
                }
            }
            .addOnFailureListener { exception ->
                callback(ArrayList())
            }
    }


}