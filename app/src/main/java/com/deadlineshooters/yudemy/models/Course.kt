package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    var id: String = "",
    var name: String = "",
    var lecturer: String = "",
    var totalStudents: Int = 0,
    var introduction: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var sectionList: ArrayList<String> = arrayListOf(),
    var promotionalVideo: Video = Video(),
    var language: String = "",
    var category: String = "",
    var totalLecture: Int = 0,
    var totalSection: Int = 0,
    var totalLength: Int = 0, // in seconds
    var totalQuiz: Int = 0,
    var thumbnail: Image = Image()
) : Parcelable
