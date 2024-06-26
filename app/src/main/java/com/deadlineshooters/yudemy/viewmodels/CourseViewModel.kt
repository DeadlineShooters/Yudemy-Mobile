package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.SectionWithLectures
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.repositories.SectionRepository
import com.google.android.gms.tasks.Task


class CourseViewModel : ViewModel() {
    private val cloudinaryHelper = CloudinaryHelper()
    private val courseRepository = CourseRepository()
    private val sectionRepository = SectionRepository()

    private val _dashboardCourses = MutableLiveData<List<Course>>()
    val dashboardCourses: LiveData<List<Course>> = _dashboardCourses

    private val _learningCourse = MutableLiveData<Course?>()
    val learningCourse: LiveData<Course?> = _learningCourse

    private val _sectionsWithLectures = MutableLiveData<List<SectionWithLectures>>()
    val sectionsWithLectures: LiveData<List<SectionWithLectures>> = _sectionsWithLectures

    private val _wishlist = MutableLiveData<List<Course>>()
    val wishlist: LiveData<List<Course>> = _wishlist

    private val _searchResult = MutableLiveData<List<Course>>()
    val searchResult: LiveData<List<Course>> = _searchResult

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    private val _editingCourse = MutableLiveData<Course?>()
    val editingCourse: LiveData<Course?> = _editingCourse

    fun refreshCourses(userId: String? = null, sortByNewest: Boolean = true) {
        val task = if (userId != null) {
            courseRepository.getCoursesByInstructor(userId, sortByNewest)
        } else {
            courseRepository.getCoursesByInstructor(sortByNewest = sortByNewest)
        }

        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _dashboardCourses.value = task.result
            } else {
                Log.d(this.javaClass.simpleName, "Failed to get courses: ${task.exception}")
            }
        }

    }

    fun refreshCourses() {
        courseRepository.getCourses { courses ->
            _courses.value = courses

            // Init data for Algolia
//            val searchHelper = SearchHelper()
//            viewModelScope.launch {
//                searchHelper.clearIndex()
//
//                courses.forEach { course ->
//                    searchHelper.indexData(course)
//                    Log.d("coroutine", course.name)
//                }
//            }
        }
    }


    fun refreshWishlist() {
        courseRepository.getWishlist { courses ->
            _wishlist.value = courses
        }
    }

    fun refreshSections(courseId: String) {
        val task = sectionRepository.getSectionsWithLectures(courseId)

        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _sectionsWithLectures.value = task.result
            } else {
                Log.d(this.javaClass.simpleName, "Failed to get sections with lectures: ${task.exception}")
            }
        }
    }

    fun deleteCourse(course: Course) {
        courseRepository.deleteCourse(course)
            .addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                _dashboardCourses.postValue(_dashboardCourses.value!!.minus(course))
            } else {
                Log.d(this.javaClass.simpleName, "Failed to delete course: ${task.exception}")
            }
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


    fun getTop3InstructorCourseList(instructorId: String) {
        courseRepository.getTop3InstructorCourseList(instructorId) {
            _courses.value = it
        }
    }

    fun getInstructorCourseList(instructorId: String) {
        courseRepository.getInstructorCourseList(instructorId) {
            _courses.value = it
        }
    }

    fun refreshSearchResult(input: String) {
        courseRepository.searchCourses(input) { courses ->
            _searchResult.value = courses
        }
    }

    fun getTotalRevenueForInstructor(instructorId: String): Task<Int> {
        return courseRepository.getCoursesByInstructor(instructorId).continueWith { task ->
            if (task.isSuccessful) {
                val courses = task.result
                courses?.sumOf { course -> course.totalRevenue } ?: 0
            } else {
                0
            }
        }
    }



}