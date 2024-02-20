package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import java.util.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reply(
    val _id: String,
    val replier: String, // id
    val questionId: String,
    val images: Image,
    val content: String,
    val createdTime: DateTime
) : Parcelable
