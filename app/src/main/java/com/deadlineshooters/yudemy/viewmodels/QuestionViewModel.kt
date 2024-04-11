package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Certificate
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.repositories.QuestionRepository

class QuestionViewModel : ViewModel() {
    private val questionRepository = QuestionRepository()
    private val _questions = MutableLiveData<ArrayList<Question>>()
    private val _question = MutableLiveData<Question>()
    private val _questionNoReplies = MutableLiveData<ArrayList<Question>>()

    val question get() = _question
    val questions get() = _questions

    val questionNoReplies get() = _questionNoReplies

    fun getQuestionsByCourseId(courseId: String) {
        questionRepository.getQuestionListByCourseId(courseId) {
            _questions.value = it
        }
    }

    fun getQuestionById(questionId:String){
        questionRepository.getQuestionById(questionId){
            _question.value = it
        }
    }

    fun addNewQuestion(courseId: String, asker: String, title: String, details: String, imageArray: ArrayList<Image>, lectureId: String, createdTime: String) {
        questionRepository.addNewQuestion(courseId, asker, title, details, imageArray, lectureId, createdTime) {
            _questions.value = it
        }
    }

    fun editQuestion(questionId: String, asker: String, title: String, details: String, imageArray: ArrayList<Image>, lectureId: String, createdTime: String) {
        questionRepository.editQuestion(questionId , asker, title, details, imageArray, lectureId, createdTime) {
            _question.value = it
        }
    }

    fun deleteQuestion(courseId: String, questionId: String) {
        questionRepository.deleteQuestion(courseId, questionId) {
            _questions.value = it
        }
    }

    fun getQuestionsOfInstructor(instructorId: String) {
        questionRepository.getQuestionsListOfInstructor(instructorId) {
            _questions.value = it
        }
    }

    fun getQuestionNoReplies(questionList: ArrayList<Question>){
        questionRepository.getQuestionNoReplies(questionList){
            _questionNoReplies.value = it
        }
    }
}