package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    var _id: String,
    var sectionId: String, // ObjectId
    var content: Video,
    var name: String,
    var index: Int
) : Parcelable

