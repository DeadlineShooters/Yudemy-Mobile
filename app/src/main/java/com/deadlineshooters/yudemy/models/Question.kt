package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Question(
    var _id: String,
    var asker: String, // object id
    var lectureId: String,
    var title: String,
    var details: String,
    var images: ArrayList<Image>,
    var createdTime: @RawValue DateTime


) : Parcelable