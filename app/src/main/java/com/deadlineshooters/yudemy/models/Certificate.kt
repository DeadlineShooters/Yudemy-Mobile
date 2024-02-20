package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.Date
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Certificate(
    val _id: String = "",
    val createdDate: Date? = null,
    val courseId: String = "",
    val title: String = "",
    val length: Int = 0 // in seconds
) : Parcelable