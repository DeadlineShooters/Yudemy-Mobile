package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SectionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val lectureRepository = LectureRepository()

    fun getSectionsWithLectures(courseId: String): Task<List<SectionWithLectures>> {
        val courseTask = mFireStore.collection("courses").document(courseId).get()
        val tasks = mutableListOf<Task<*>>()
        val lectureTasks = mutableListOf<Task<*>>()

        return courseTask.continueWithTask { task ->
            val course = task.result!!.toObject(Course::class.java)
            val sectionsWithLectures = mutableListOf<SectionWithLectures>()


            for (sectionId in course!!.sectionList) {
                val sectionTask = mFireStore.collection("sections").document(sectionId).get()
                tasks.add(sectionTask)
            }

            Tasks.whenAllComplete(tasks).continueWithTask { _ ->
                for (i in tasks.indices) {
                    val section = (tasks[i].result as DocumentSnapshot).toObject(Section::class.java)
                    section?._id = (tasks[i].result as DocumentSnapshot).id
                    val lectureTask = section?.let { lectureRepository.getLecturesBySectionId(it._id) }
                    if (lectureTask != null) {
                        lectureTasks.add(lectureTask)
                    }
                }
                Tasks.whenAllComplete(lectureTasks)
            }.continueWith { task ->
                for (i in tasks.indices) {
                    val section = (tasks[i].result as DocumentSnapshot).toObject(Section::class.java)
                    val lectures = lectureTasks[i].result as List<Lecture>
                    if (section != null) {
                        Log.d(this.javaClass.simpleName, section.title)
                    } else {
                        Log.d(this.javaClass.simpleName, "Section is null")

                    }
                    sectionsWithLectures.add(SectionWithLectures(section!!, lectures))
                }
                sectionsWithLectures
            }
        }
    }






}