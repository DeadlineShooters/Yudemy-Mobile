package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.UserLecture
import com.deadlineshooters.yudemy.repositories.UserLectureRepository

class UserLectureViewModel : ViewModel() {
    private val userLectureRepository = UserLectureRepository()

    private val _userLectures = MutableLiveData<List<UserLecture>>()

    val userLectures: LiveData<List<UserLecture>> = _userLectures

    fun refreshData(userId: String) {
    }
}