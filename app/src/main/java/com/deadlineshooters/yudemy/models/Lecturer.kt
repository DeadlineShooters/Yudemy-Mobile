package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecturer(
    val _id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val headline: String = "",
    val totalReviews: Int = 0,
    val totalStudents: Int = 0,
    val bio: String = "",
    val image: Image
) : Parcelable