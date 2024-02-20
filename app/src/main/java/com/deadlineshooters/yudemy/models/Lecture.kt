package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    val _id: String,
    val sectionId: String, // ObjectId
    val content: LectureContent,
    val name: String,
    val type: String,
    val index: Int
) : Parcelable

