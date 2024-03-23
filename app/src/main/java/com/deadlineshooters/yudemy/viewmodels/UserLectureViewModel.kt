package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.repositories.UserLectureRepository

class UserLectureViewModel : ViewModel() {
    private val userLectureRepository = UserLectureRepository()

    private val _userLectures = MutableLiveData<ArrayList<ArrayList<Map<Lecture, Boolean>>>>()
    val userLectures: LiveData<ArrayList<ArrayList<Map<Lecture, Boolean>>>> = _userLectures

    fun getUserLecturesByCourse(userId: String, courseId: String) {
        userLectureRepository.getUserLecturesByCourse(userId, courseId) { lectures ->
            _userLectures.value = lectures
        }
    }
}