package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Video
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
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
    fun getLectureById(lectureId: String, callback: (Lecture) -> Unit){
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

    fun getLectureListByCourseId(courseId: String, callback: (ArrayList<Lecture>) -> Unit){
        mFireStore.collection("courses")
            .document(courseId)
            .get()
            .addOnSuccessListener { result ->
                val sectionList: ArrayList<String> = result.data?.get("sectionList") as ArrayList<String>
                val lectureTasks = sectionList.map{section ->
                    val task = TaskCompletionSource<Lecture>()
                    lecturesCollection.whereEqualTo("sectionId", section).get()
                        .addOnSuccessListener { lectureList ->
                            for(lecture in lectureList){
                                val lectureId = lecture.id
                                getLectureById(lectureId, callback = {
                                    task.setResult(it)
                                })
                            }
                        }
                    task.task
                }
                Tasks.whenAllSuccess<Lecture>(lectureTasks).addOnSuccessListener {
                    callback(it as ArrayList<Lecture>)
                }
            }
            .addOnFailureListener { exception ->
                callback(ArrayList())
            }
    }

    fun addLecture(lecture: Lecture): Task<String> {
        return lecturesCollection
            .add(lecture)
            .continueWith{
                it.result.id
            }
    }
}