package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Category
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class CategoryRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val categoriesCollection = mFireStore.collection(Constants.CATEGORIES)

    fun getCategories(): Task<List<Category>> {
        val task = categoriesCollection.get()

        return task.continueWith { task ->
            if (task.isSuccessful) {
                val result = task.result
                val categories = result?.map { document ->
                    document.toObject(Category::class.java)
                } ?: emptyList()
                categories
            } else {
                emptyList()
            }
        }
    }

    fun loadCategory(categoryId: String, callback: (Category?) -> Unit) {
        if (categoryId != "") {
            categoriesCollection.document(categoryId)
                .get()
                .addOnSuccessListener { document ->
                    val category = document?.toObject(Category::class.java)
                    callback(category)
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting category: ", exception)
                    callback(null)
                }
        } else callback(null)

    }

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