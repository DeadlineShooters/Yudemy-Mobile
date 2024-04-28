package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Parcelize
data class FeedbackResponse(
    var instructorId: String = "",
    var content: String = "",
    var createdDatetime: String = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ssZ",
        Locale.getDefault()
    ).format(
        Date()
    )

) : Parcelable