package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Course(
    var id: String = "",
    var name: String = "",
    var instructor: String = "",
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
    var thumbnail: Image = Image(),
    var avgRating: Number = 0,
    var status: Boolean = false,
    var createdDate: @RawValue DateTime,
    var totalRevenue: Number = 0.0
) : Parcelable
