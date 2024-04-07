package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseProgressRepository
import com.deadlineshooters.yudemy.repositories.UserRepository

class CourseProgressViewModel: ViewModel() {
    private val courseProgressRepository = CourseProgressRepository()

    private val _mylearningCourses = MutableLiveData<ArrayList<Map<Course, String>>>()
    val mylearningCourses: LiveData<ArrayList<Map<Course, String>>> = _mylearningCourses

    private val _myCoursesProgress = MutableLiveData<ArrayList<Number>>()
    val myCoursesProgress: LiveData<ArrayList<Number>> = _myCoursesProgress

    private val _myFavoriteCourseIds = MutableLiveData<ArrayList<String>>()
    val myFavoriteCourseIds: LiveData<ArrayList<String>> = _myFavoriteCourseIds

    val combinedData = MediatorLiveData<Pair<ArrayList<Map<Course, String>>, ArrayList<Number>>>().apply {
        addSource(mylearningCourses) { courses ->
            value = Pair(courses, myCoursesProgress.value ?: arrayListOf())
        }
        addSource(myCoursesProgress) { progresses ->
            value = Pair(mylearningCourses.value ?: arrayListOf(), progresses)
        }
    }

    fun getUserCourses() {
        UserRepository().getUserCourses { courses, progresses ->
            _mylearningCourses.value = courses
            _myCoursesProgress.value = progresses
        }
    }

    fun getUserFavoriteCourseIds() {
        UserRepository().getUserFavoriteCourseIds { courseIds ->
            _myFavoriteCourseIds.value = courseIds
        }
    }

    fun refreshCourseProgress(position: Int, course: Course) {
        courseProgressRepository.getCourseProgressByCourse(course.id) { progress ->
            _myCoursesProgress.postValue(_myCoursesProgress.value.apply { this?.set(position, Math.toIntExact(progress.toLong())) })
        }
    }

    fun updateCourseProgress(courseId: String, percentCompleted: Number) {
        courseProgressRepository.updateCourseProgress(courseId, percentCompleted)
    }
}