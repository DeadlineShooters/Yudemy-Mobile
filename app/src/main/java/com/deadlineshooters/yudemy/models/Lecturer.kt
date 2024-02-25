package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecturer(
    var _id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var headline: String = "",
    var totalReviews: Int = 0,
    var totalStudents: Int = 0,
    var bio: String = "",
    var image: Image = Image()
) : Parcelable