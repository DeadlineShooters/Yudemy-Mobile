package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.repositories.SectionRepository

class SectionViewModel : ViewModel() {
    private val sectionRepository = SectionRepository()

    private val _sectionsCourseLearning = MutableLiveData<ArrayList<Section>>()
    val sectionsCourseLearning: LiveData<ArrayList<Section>> = _sectionsCourseLearning

    fun getSectionsCourseLearning(courseId: String) {
        val sectionIds = CourseRepository().getSectionIdListOfCourse(courseId)
        sectionIds?.forEach {
            sectionRepository.getSectionById(it)
                ?.let { it1 -> sectionsCourseLearning.value?.add(it1) }
        }
    }
}