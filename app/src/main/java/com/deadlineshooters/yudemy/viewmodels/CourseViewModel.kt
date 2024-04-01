package com.deadlineshooters.yudemy.viewmodels

import android.content.ComponentCallbacks
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.repositories.CourseRepository


class CourseViewModel : ViewModel() {
    private val courseRepository = CourseRepository()
    private val cloudinaryHelper = CloudinaryHelper()
    val courses: LiveData<List<Course>> = courseRepository.getCourses()

    private val _courses = MutableLiveData<List<Course>>()
    val course get() = _courses

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

}