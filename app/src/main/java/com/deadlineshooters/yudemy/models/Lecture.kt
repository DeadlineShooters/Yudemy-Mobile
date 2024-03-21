package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
data class Lecture(
    var _id: String,
    var sectionId: String, // ObjectId
    var content: Video,
    var name: String,
    var type: String,
    var index: Int
) : Parcelable

