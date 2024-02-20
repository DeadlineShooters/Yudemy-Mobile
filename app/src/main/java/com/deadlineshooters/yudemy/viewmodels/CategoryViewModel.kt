package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.models.Category
import com.deadlineshooters.yudemy.repositories.CategoryRepository

class CategoryViewModel {
    private val categoryRepository = CategoryRepository()

    // feel free to change the below
    private val _categoryList = MutableLiveData<List<Category>>()
    val weatherData: LiveData<List<Category>> = _categoryList


}