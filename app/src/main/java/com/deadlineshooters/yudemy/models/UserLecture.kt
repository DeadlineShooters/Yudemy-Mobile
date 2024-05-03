package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLecture(
    @DocumentId
    var _id: String = "",
    var userId: String,
    var lectureId: String,
    var finished: Boolean
) : Parcelable
