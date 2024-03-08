package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FeedbackResponse(
    var instructorId: String = "",
    var content: String = "",
    var createdTime: @RawValue DateTime

) : Parcelable