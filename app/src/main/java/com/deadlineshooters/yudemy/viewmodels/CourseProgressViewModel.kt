package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.CourseProgressRepository

class CourseProgressViewModel: ViewModel() {
    private val courseProgressRepository = CourseProgressRepository()

    private val _coursesProgress = MutableLiveData<ArrayList<Int>>()
    val coursesProgress = _coursesProgress
}