package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Category
import com.google.firebase.firestore.FirebaseFirestore

class CategoryRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val categoryCollection = mFireStore.collection("categories")

    fun getCategory(categoryId: String, callback: (Category) -> Unit) {
        if (categoryId == "") {
            callback(Category())
            return
        }
        categoryCollection
            .document(categoryId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val category = document.toObject(Category::class.java)!!
                    callback(category)
                } else {
                    Log.d("Firestore", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }

}