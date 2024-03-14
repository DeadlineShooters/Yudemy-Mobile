package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Instructor(
    var _id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var headline: String = "",
    var totalReviews: Int = 0,
    var totalStudents: Int = 0,
    var bio: String = "",
    var image: Image = Image(),
    var walletId: String = "",
    var walletName: String = ""
) : Parcelable