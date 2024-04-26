package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Language
import com.google.firebase.firestore.FirebaseFirestore

class LanguageRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val languageCollection = mFireStore.collection("languages")

    fun getLanguage(languageId: String, callback: (Language) -> Unit) {
        if (languageId == "") {
            callback(Language())
            return
        }
        languageCollection
            .document(languageId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val language = document.toObject(Language::class.java)!!
                    callback(language)
                } else {
                    Log.d("Firestore", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }
}