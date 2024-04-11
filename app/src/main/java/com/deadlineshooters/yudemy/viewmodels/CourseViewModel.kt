package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository


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

//    fun addDummyCourse() {
//
//        val imageFilePath = "/storage/emulated/0/Android/data/com.deadlineshooters.yudemy/files/DCIM/img_thumbnail.jpg"
//        val videoFilePath = "/storage/emulated/0/Android/data/com.deadlineshooters.yudemy/files/DCIM/2 - Thanks.mp4"
//
//        // Upload image
//        cloudinaryHelper.uploadMedia(imageFilePath) { img ->
//            // Handle the uploaded image
//            // Upload video
//            cloudinaryHelper.uploadMedia(videoFilePath, true) { vid ->
//                // Handle the uploaded video
//                // Generate dummy course and add it to the repository
//                val dummyCourse = courseRepository.generateDummyCourse(img as Image, vid as Video)
//                courseRepository.addCourse(dummyCourse)
//            }
//        }
//
//    }
}