package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import android.content.ContentValues
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FirebaseFirestore

class SectionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val sectionCollection = mFireStore.collection("sections")
    private val coursesCollection = mFireStore.collection(Constants.COURSES)

    private val lectureRepository = LectureRepository()

    fun getSectionsWithLectures(courseId: String): Task<List<SectionWithLectures>> {
        val courseTask = coursesCollection.document(courseId).get()
        val tasks = mutableListOf<Task<*>>()
        val lectureTasks = mutableListOf<Task<*>>()

        return courseTask.continueWithTask { task ->
            val course = task.result!!.toObject(Course::class.java)
            val sectionsWithLectures = mutableListOf<SectionWithLectures>()


            for (sectionId in course!!.sectionList) {
                val sectionTask = sectionCollection.document(sectionId).get()
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






    fun getSectionDetailById(sectionId: String, callback: (Section?) -> Unit) {
        var section: Section?
        sectionCollection
            .document(sectionId)
            .get()
            .addOnSuccessListener { document ->
                section = document?.toObject(Section::class.java)
                callback(section)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(null)
            }
    }

    fun getSectionsOfCourse(courseId: String, callback: (ArrayList<Section>) -> Unit) {
        val sectionList: ArrayList<Section> = arrayListOf()

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

                    val sectionRepository = SectionRepository()
                    val tasks = idList.map { id ->
                        val task = TaskCompletionSource<Section>()
                        sectionRepository.getSectionDetailById(id) { section ->
                            section?.let { task.setResult(section) }
                        }
                        task.task
                    }
                    Tasks.whenAllSuccess<Section>(tasks)
                        .addOnSuccessListener { sections ->
                            callback(ArrayList(sections))
                        }
                } else {
                    Log.d("Firestore", "No such document")
                    callback(sectionList)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(sectionList)
            }
    }
}