package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class UserLecture(
    @get:Exclude
    var _id: String,
    var userId: String,
    var lectureId: String,
    var finished: Boolean
) : Parcelable
