package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@kotlinx.parcelize.Parcelize
data class Course(
    @DocumentId
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
    var totalRatings: Int = 0,
    var avgRating: Double = 0.0,
    var status: Boolean = false,
    var createdDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    var totalRevenue: Number = 0.0,
    var fiveStarCnt: Int = 0,
    var fourStarCnt: Int = 0,
    var threeStarCnt: Int = 0,
    var twoStarCnt: Int = 0,
    var oneStarCnt: Int = 0,


) : Parcelable