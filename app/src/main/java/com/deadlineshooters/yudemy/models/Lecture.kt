package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    @DocumentId
    var _id: String = "",
    var sectionId: String = "", // ObjectId
    var content: Video = Video("", "", "", 0.0),
    var name: String = "",
    var type: String = "",
    var index: Int = 0
) : Parcelable

