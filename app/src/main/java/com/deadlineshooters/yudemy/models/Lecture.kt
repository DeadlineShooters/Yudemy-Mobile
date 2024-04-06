package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    var _id: String = "",
    var sectionId: String = "", // ObjectId
    var content: Video = Video(),
    var name: String = "",
    var type: String = "video",
    var index: Int = 0
) : Parcelable

