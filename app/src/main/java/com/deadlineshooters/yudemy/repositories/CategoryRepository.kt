package com.deadlineshooters.yudemy.repositories

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


}