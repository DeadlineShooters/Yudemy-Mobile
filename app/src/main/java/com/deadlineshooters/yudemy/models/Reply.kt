package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import java.util.*
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Reply(
    var _id: String,
    var replier: String, // id
    var questionId: String,
    var images: Image,
    var content: String,
    var createdTime: @RawValue DateTime
) : Parcelable
