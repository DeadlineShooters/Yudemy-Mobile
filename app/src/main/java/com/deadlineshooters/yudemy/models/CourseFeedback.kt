package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import java.util.*
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CourseFeedback(
    var _id: String,
    var courseId: String,
    var userId: String,
    var feedback: String,
    var rating: Number,
    var createdDatetime: @RawValue DateTime
) : Parcelable
