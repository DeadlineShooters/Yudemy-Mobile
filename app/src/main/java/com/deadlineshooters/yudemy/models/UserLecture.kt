package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
data class UserLecture(
    var _id: String,
    var userId: String,
    var lectureId: String,
    var watchedTime: Long, // in seconds
    var minuteInVid: Timestamp
) : Parcelable
