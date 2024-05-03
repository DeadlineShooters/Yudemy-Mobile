package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.models.Category
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class CategoryRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val categoryCollection = mFireStore.collection("categories")

    fun getCategories(): Task<List<Category>> {
        return categoryCollection.get().continueWith { task ->
            task.result.toObjects(Category::class.java)
        }
    }
}