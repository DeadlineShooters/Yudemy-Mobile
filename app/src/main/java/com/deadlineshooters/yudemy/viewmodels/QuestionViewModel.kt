package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.QuestionRepository

class QuestionViewModel : ViewModel() {
    private val questionRepository = QuestionRepository()

}