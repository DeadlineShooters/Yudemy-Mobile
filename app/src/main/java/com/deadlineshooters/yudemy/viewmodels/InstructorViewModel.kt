package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.repositories.InstructorRepository

class InstructorViewModel : ViewModel(){
    private val instructorRepository = InstructorRepository()
    val instructors: LiveData<List<Instructor>> = instructorRepository.getInstructor()
}