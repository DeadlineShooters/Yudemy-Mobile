package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    val id: String = "",
    val name: String = "",
    val lecturer: String = "",
    val totalStudents: Int = 0,
    val introduction: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val sectionList: ArrayList<String> = arrayListOf(),
    val requirements: String = "",
    val previewLecture: String = "",
    val language: String = "",
    val category: String = "",
    val totalLecture: Int = 0,
    val totalSection: Int = 0,
    val totalLength: Int = 0, // in seconds
    val totalQuiz: Int = 0
) : Parcelable
