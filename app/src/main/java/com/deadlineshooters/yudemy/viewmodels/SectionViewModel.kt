package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.repositories.SectionRepository

class SectionViewModel : ViewModel() {
    private val sectionRepository = SectionRepository()

    private var _sectionsCourseLearning = MutableLiveData<ArrayList<Section>>()
    val sectionsCourseLearning: LiveData<ArrayList<Section>> = _sectionsCourseLearning

    fun getSectionsCourseLearning(courseId: String) {
        sectionRepository.getSectionsOfCourse(courseId) { sections ->
            _sectionsCourseLearning.value = sections
        }
    }
}