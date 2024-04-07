package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.repositories.SectionRepository
import com.deadlineshooters.yudemy.repositories.UserLectureRepository

class UserLectureViewModel : ViewModel() {
    private val userLectureRepository = UserLectureRepository()

    private val _userLectures = MutableLiveData<ArrayList<ArrayList<Map<Lecture, Boolean>>>>()
    val userLectures: LiveData<ArrayList<ArrayList<Map<Lecture, Boolean>>>> = _userLectures

    private var _sectionsCourseLearning = MutableLiveData<ArrayList<Section>>()
    val sectionsCourseLearning: LiveData<ArrayList<Section>> = _sectionsCourseLearning


    val combinedData = MediatorLiveData<Pair<ArrayList<Section>, ArrayList<ArrayList<Map<Lecture, Boolean>>>>>().apply {
        addSource(sectionsCourseLearning) { sections ->
            if (userLectures.value != null) {
                value = Pair(sections, userLectures.value!!)
            }
        }
        addSource(userLectures) { userLectures ->
            if (sectionsCourseLearning.value != null) {
                value = Pair(sectionsCourseLearning.value!!, userLectures)
            }
        }
    }

    fun getSectionsCourseLearning(courseId: String) {
        SectionRepository().getSectionsOfCourse(courseId) { sections ->
            _sectionsCourseLearning.value = sections
        }
    }

    fun getUserLecturesByCourse(courseId: String) {
        Log.d("LectureLearningFragment", "getUserLecturesByCourse: $courseId")
        userLectureRepository.getUserLecturesByCourse(courseId) { lectures ->
            Log.d("LectureLearningFragment", "getUserLecturesByCourse: $lectures")
            _userLectures.value = lectures
        }
    }

    fun getLectureLearningData(courseId: String) {
        getSectionsCourseLearning(courseId)
        getUserLecturesByCourse(courseId)
    }

    fun markLecture(lectureId: String, isFinished: Boolean, sectionIdx: Int, lectureIdx: Int) {
        userLectureRepository.markLecture(lectureId, isFinished)
        _userLectures.value?.get(sectionIdx)?.get(lectureIdx)?.values?.first()?.let {
            _userLectures.value?.get(sectionIdx)?.set(lectureIdx, mapOf(_userLectures.value?.get(sectionIdx)?.get(lectureIdx)?.keys?.first()!! to isFinished))
        }
    }
}