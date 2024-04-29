package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Video
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
                    val lectures = lectureTasks[i].result as ArrayList<Lecture>
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

    fun addSection(section: Section): Task<String> {
        return sectionCollection.add(section)
            .continueWith { task ->
                task.result.id
            }
    }

    fun addSectionsWithLecture(sections: ArrayList<SectionWithLectures>, course: Course): Task<Void> {
        val tasks = mutableListOf<Task<*>>()
        for (section in sections) {
            val task = addSection(section.section).continueWithTask { task ->
                val sectionId = task.result
                section.section._id = sectionId
                CourseRepository().addASection(course.id, sectionId)
                    .continueWithTask {
                        course.sectionList.add(sectionId)

                        val lectureTasks = mutableListOf<Task<*>>()
                        for (lecture in section.lectures) {
                            val tcs = TaskCompletionSource<Void>()
                            CloudinaryHelper.uploadMedia(
                                fileUri = lecture.content.contentUri,
                                isVideo = true
                            ) { media ->
                                lecture.content = media as Video
                                lecture.sectionId = sectionId
                                LectureRepository().addALecture(lecture, course).continueWith {
                                    lecture._id = it.result
                                    tcs.setResult(null)
                                }
                            }
                            lectureTasks.add(tcs.task)
                        }
                        Tasks.whenAllComplete(lectureTasks)
                    }
            }
            tasks.add(task)
        }
        return Tasks.whenAllComplete(tasks).continueWith { task ->
            null
        }
    }

    fun updateSections(sections: List<Section>): Task<Void> {
        val tasks = sections.map { section ->
            sectionCollection.document(section._id).set(section)
        }
        return Tasks.whenAll(tasks)
    }

    fun deleteSectionsWithLectures(sections: List<SectionWithLectures>, course: Course): Task<Void> {
        val tasks = sections.map { section ->
            sectionCollection.document(section.section._id).delete()
                .continueWithTask {
                    LectureRepository().deleteLectures(section.lectures, course)
                        .continueWithTask {
                            CourseRepository().deleteSection(course.id, section.section._id)
                            course.sectionList.remove(section.section._id)
                            it
                        }
                }
        }
        return Tasks.whenAll(tasks)
    }

    fun updateIndexes(sections: List<Section>): Task<Void> {
        val tasks = sections.map { section ->
            sectionCollection.document(section._id)
                .update("index", section.index)
        }
        return Tasks.whenAll(tasks)
    }
}