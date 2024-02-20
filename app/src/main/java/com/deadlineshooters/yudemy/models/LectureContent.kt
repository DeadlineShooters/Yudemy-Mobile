package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LectureContent(
    val secure_url: String,
    val public_id: String,
    val resource_type: String,
    val video_duration: Int
) : Parcelable