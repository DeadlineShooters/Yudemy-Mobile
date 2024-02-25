package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.LecturerRepository

class LecturerViewModel : ViewModel(){
    private val lecturerRepository = LecturerRepository()

}