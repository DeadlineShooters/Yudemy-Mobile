package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.QuizRepository

class QuizViewModel : ViewModel(){
    private val quizRepository = QuizRepository()

}