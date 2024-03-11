package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.UserLectureRepository

class UserLectureViewModel : ViewModel() {
    private val userLectureRepository = UserLectureRepository()

}