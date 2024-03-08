package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.InstructorRepository

class InstructorViewModel : ViewModel(){
    private val instructorRepository = InstructorRepository()

}