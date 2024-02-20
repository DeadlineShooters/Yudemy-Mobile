package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
    val _id: String,
    val asker: String, // object id
    val lectureId: String,
    val title: String,
    val details: String,
    val images: ArrayList<Image>,
    val createdTime: DateTime


) : Parcelable