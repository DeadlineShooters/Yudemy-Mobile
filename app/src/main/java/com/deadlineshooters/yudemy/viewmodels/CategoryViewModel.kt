package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Category
import com.deadlineshooters.yudemy.repositories.CategoryRepository

class CategoryViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository()

    // feel free to change the below
    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    fun refreshCategories() {
        val task = categoryRepository.getCategories()

        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _categoryList.value = task.result
            } else {
                Log.d(this.javaClass.simpleName, "Failed to get categories: ${task.exception}")
            }
        }

    }
}