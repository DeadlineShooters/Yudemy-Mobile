package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Certificate
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.InstructorRepository

class InstructorViewModel : ViewModel(){
    private val instructorRepository = InstructorRepository()
    val instructors: LiveData<List<Instructor>> = instructorRepository.getInstructor()

    private val _instructor = MutableLiveData<User>()
    val instructor get() = _instructor

    fun getInstructorByCourse(courseId: String) {
        instructorRepository.getInstructorByCourseId(courseId) {
            _instructor.value = it
        }
    }

    fun getInstructorById(instructorId: String) {
        instructorRepository.getInstructorById(instructorId) {
            _instructor.value = it
        }
    }

    fun modifyInstructorProfile(instructorId: String, fullName: String, headline: String, bio: String){
        instructorRepository.modifyInstructorProfile(instructorId, fullName, headline, bio){
            _instructor.value = it
        }
    }

}