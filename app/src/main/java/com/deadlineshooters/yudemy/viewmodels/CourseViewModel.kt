package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.helpers.SearchHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository
import kotlinx.coroutines.launch


class CourseViewModel : ViewModel() {
    private val cloudinaryHelper = CloudinaryHelper()
    private val courseRepository = CourseRepository()

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    private val _wishlist = MutableLiveData<List<Course>>()
    val wishlist: LiveData<List<Course>> = _wishlist

    private val _searchResult = MutableLiveData<List<Course>>()
    val searchResult: LiveData<List<Course>> = _searchResult


    fun refreshCourses() {
        courseRepository.getCourses { courses ->
            _courses.value = courses

            // Init data for Algolia
            val searchHelper = SearchHelper()
            for (course in courses) {
                viewModelScope.launch {
                    searchHelper.indexData(course)
                }
                Log.d("coroutine", course.name)
            }
        }
    }

    fun refreshWishlist() {
        courseRepository.getWishlist { courses ->
            _wishlist.value = courses
        }
    }

    fun refreshSearchResult(input: String) {
        courseRepository.searchCourses(input) { courses ->
            _searchResult.value = courses
        }
    }

}