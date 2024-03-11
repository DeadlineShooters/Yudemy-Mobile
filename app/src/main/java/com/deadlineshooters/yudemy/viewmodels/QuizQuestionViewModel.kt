package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.QuizQuestionRepository

class QuizQuestionViewModel : ViewModel() {
    private val quizQuestionRepository = QuizQuestionRepository()

}