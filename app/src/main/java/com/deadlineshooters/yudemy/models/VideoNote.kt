package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoNote(
    var _id: String,
    var userId: String,
    var lectureId: String,
    var noteContent: String,
    var minuteInVid: Timestamp
) : Parcelable
