package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.adapters.FeedbackAdapter
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository
import com.deadlineshooters.yudemy.repositories.UserRepository


class CourseFeedbackViewModel : ViewModel() {
    private val courseFeedbackRepository = CourseFeedbackRepository()

    private val _feedback_íntructor = MutableLiveData<List<CourseFeedback>>()
    val feedback_íntructor: LiveData<List<CourseFeedback>> = _feedback_íntructor

    fun refreshCourseFeedbackForInstructor(noInstructorResponse : Boolean?) {
       val task =  courseFeedbackRepository.getAllInstructorFeedback(UserRepository.getCurrentUserID(), noInstructorResponse)

        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _feedback_íntructor.value = task.result
            } else {
                Log.d(this.javaClass.simpleName, "Failed to get course feedback: ${task.exception}")
            }
        }
    }
}