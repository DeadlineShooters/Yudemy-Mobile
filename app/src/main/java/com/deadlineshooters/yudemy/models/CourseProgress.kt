package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseProgress(
    @DocumentId
    var _id: String = "",
    var courseId: String = "",
    var userId: String = "",
    var percentCompleted: Int = 0
): Parcelable