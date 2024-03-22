package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.models.Section
import com.google.firebase.firestore.FirebaseFirestore

class SectionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val sectionCollection = mFireStore.collection("sections")

    fun getSectionById(sectionId: String): Section? {
        var section: Section? = null
        sectionCollection
            .document(sectionId)
            .get()
            .addOnSuccessListener { document ->
                section = document.toObject(Section::class.java)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
        return section
    }
}