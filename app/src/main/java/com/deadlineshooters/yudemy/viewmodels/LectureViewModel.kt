package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.repositories.InstructorRepository

class LectureViewModel : ViewModel() {
    private val lectureRepository = InstructorRepository()

    fun addDummyInstructor() {
//        lectureRepository.addInstructor(
//            Instructor(
//                firstName = "Prateek",
//                lastName = "Narang",
//                headline = "Instructor & Entrepreneur - Google, Coding Minutes, Scaler",
//                bio = "Prateek is popular programming instructor and an ace software engineer having worked with Google in the past, currently working with Scaler and created Coding Minutes to bring high quality courses at pocket friendly pricing. He is known for his amazingly simplified explanations that makes everyone fall in love with programming. He has has over 5+ years of teaching experience and has trained over 50,000 students in classroom bootcamps & online course at a popular bootcamp in the past. His expertise lies in problem solving, algorithms, competitive programming and machine learning. His interactive mario style at prateeknarang resume is loved by all. Many of his ex-students are now working in top product companies like Apple, Google, Amazon, PayTm, Microsoft, Flipkart, Samsung, Adobe, DE Shaw, Codenation, Arcesium and more."
//            )
//        )
    }
}