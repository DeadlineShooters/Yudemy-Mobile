package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import java.util.*
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CourseFeedback(
    val _id: String,
    val courseId: String,
    val userId: String,
    val feedback: String,
    val rating: Number,
    val createdDatetime: @RawValue DateTime
) : Parcelable
