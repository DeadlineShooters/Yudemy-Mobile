package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@kotlinx.parcelize.Parcelize
data class Question(
    var _id: String,
    var asker: String, // object id
    var lectureId: String,
    var title: String,
    var details: String,
    var images: ArrayList<Image>,
    var createdTime: String = "01/01/2003" // dd/mm/yyyy


) : Parcelable