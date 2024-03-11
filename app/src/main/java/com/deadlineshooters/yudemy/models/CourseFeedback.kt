package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class CourseFeedback(
    var _id: String = "",
    var courseId: String = "",
    var userId: String = "",
    var feedback: String = "",
    var rating: Number = 5,
    var createdDatetime: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(
        Date()
    ),
    var instructorResponse: FeedbackResponse = FeedbackResponse()
) : Parcelable
