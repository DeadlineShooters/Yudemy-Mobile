package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLecture(
    val _id: String,
    val userId: String,
    val lectureId: String,
    val watchedTime: Long, // in seconds
    val minuteInVid: Timestamp
) : Parcelable
