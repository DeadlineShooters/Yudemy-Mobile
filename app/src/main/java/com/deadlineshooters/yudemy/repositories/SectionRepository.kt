package com.deadlineshooters.yudemy.repositories

import android.content.ContentValues
import android.util.Log
import com.deadlineshooters.yudemy.models.Section
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class SectionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val sectionCollection = mFireStore.collection("sections")

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
                    val idList = document.data?.get("sectionList") as ArrayList<String>
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
                            callback(sections as ArrayList<Section>)
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