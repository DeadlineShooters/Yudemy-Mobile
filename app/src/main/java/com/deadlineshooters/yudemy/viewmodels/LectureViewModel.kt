package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.InstructorRepository
import com.deadlineshooters.yudemy.repositories.LectureRepository

class LectureViewModel : ViewModel() {
    private val lectureRepository = LectureRepository()
    private val _lectures = MutableLiveData<ArrayList<Lecture>>()
    private val _lecture = MutableLiveData<Lecture>()

    val lecture get() = _lecture
    val lectures get() = _lectures

//    fun addDummyInstructor() {
//        lectureRepository.addInstructor(
//            User(
//                "Prateek", arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), false, "", Instructor(
//                "Instructor & Entrepreneur - Google, Coding Minutes, Scaler", 123, 123,
//                "Prateek is popular programming instructor and an ace software engineer having worked with Google in the past, currently working with Scaler and created Coding Minutes to bring high quality courses at pocket friendly pricing. He is known for his amazingly simplified explanations that makes everyone fall in love with programming. He has has over 5+ years of teaching experience and has trained over 50,000 students in classroom bootcamps & online course at a popular bootcamp in the past. His expertise lies in problem solving, algorithms, competitive programming and machine learning. His interactive mario style at prateeknarang resume is loved by all. Many of his ex-students are now working in top product companies like Apple, Google, Amazon, PayTm, Microsoft, Flipkart, Samsung, Adobe, DE Shaw, Codenation, Arcesium and more."
//                )
//            )
//        )
//    }

    fun getLectureById(lectureId: String) {
        lectureRepository.getLectureById(lectureId) {
            _lecture.value = it
        }
    }

    fun getLectureListByCourseId(courseId: String) {
        lectureRepository.getLectureListByCourseId(courseId) {
            _lectures.value = it
        }
    }
}