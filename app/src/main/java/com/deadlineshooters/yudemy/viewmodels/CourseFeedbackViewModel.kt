package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository


class CourseFeedbackViewModel : ViewModel() {
    private val courseFeedbackRepository = CourseFeedbackRepository()

}