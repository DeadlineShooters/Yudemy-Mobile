package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class LectureRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lecturesCollection = mFireStore.collection(Constants.LECTURES)
    private val courseRepository = mFireStore.collection(Constants.COURSES)

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

    fun addALecture(lecture: Lecture, course: Course): Task<String> {
        return lecturesCollection
            .add(lecture)
            .continueWithTask{ lectureTask ->
                CourseRepository().updateTotal(lecture, course, true).continueWith {
                    lectureTask.result.id
                }
            }
    }

    fun addLectures(lectures: List<Lecture>, course: Course): Task<List<String>> {
        val tasks = lectures.map { lecture ->
            val tcs = TaskCompletionSource<String>()
            CloudinaryHelper.uploadMedia(
                fileUri = lecture.content.contentUri,
                isVideo = true
            ) { media ->
                lecture.content = media as Video
                LectureRepository().addALecture(lecture, course).addOnSuccessListener {
                    lecture._id = it
                    tcs.setResult(it)
                }
            }
            tcs.task
        }
        return Tasks.whenAllSuccess(tasks)
    }

    fun updateLectures(lectures: List<Lecture>, course: Course): Task<Void> {
        val tasks = lectures.map { lecture ->
            lecturesCollection.document(lecture._id).set(lecture)

            val tcs = TaskCompletionSource<Void>()
            if(lecture.content.contentUri != null) {
                CloudinaryHelper.uploadMedia(
                    fileUri = lecture.content.contentUri,
                    isVideo = true
                ) { media ->
                    val uploadedVideo = media as Video
                    CourseRepository().updateTotalLength(course, lecture.content.duration.toInt(), uploadedVideo.duration.toInt())
                    lecture.content = uploadedVideo
                    tcs.setResult(null)
                }
            }
            else
                tcs.setResult(null)

            tcs.task.continueWithTask {
                lecturesCollection.document(lecture._id).set(lecture)
            }
        }
        return Tasks.whenAll(tasks)
    }

    fun deleteLectures(lectures: List<Lecture>, course: Course): Task<Void> {
        val tasks = lectures.map { lecture ->
            lecturesCollection
                .document(lecture._id)
                .delete()
                .continueWithTask {
                    CourseRepository().updateTotal(lecture, course, false)
                }
        }
        return Tasks.whenAll(tasks)
    }

    fun updateIndexes(lectures: List<Lecture>): Task<Void> {
        val tasks = lectures.map { lecture ->
            lecturesCollection.document(lecture._id)
                .update("index", lecture.index)
        }
        return Tasks.whenAll(tasks)
    }
}