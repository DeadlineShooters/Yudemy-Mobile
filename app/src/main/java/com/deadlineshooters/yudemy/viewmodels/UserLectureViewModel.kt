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

    fun getUserLecturesByCourse(courseId: String) {
        userLectureRepository.getUserLecturesByCourse(courseId) { lectures ->
            _userLectures.value = lectures
        }
    }

    fun markLecture(lectureId: String, isFinished: Boolean, sectionIdx: Int, lectureIdx: Int) {
        userLectureRepository.markLecture(lectureId, isFinished)
        _userLectures.value?.get(sectionIdx)?.get(lectureIdx)?.values?.first()?.let {
            _userLectures.value?.get(sectionIdx)?.set(lectureIdx, mapOf(_userLectures.value?.get(sectionIdx)?.get(lectureIdx)?.keys?.first()!! to isFinished))
        }
    }
}