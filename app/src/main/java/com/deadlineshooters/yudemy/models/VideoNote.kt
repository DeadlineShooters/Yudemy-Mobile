package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoNote(
    val _id: String,
    val userId: String,
    val lectureId: String,
    val noteContent: String,
    val minuteInVid: Timestamp
) : Parcelable
