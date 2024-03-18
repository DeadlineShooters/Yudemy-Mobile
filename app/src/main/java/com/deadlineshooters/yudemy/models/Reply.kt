package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import java.util.*
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Reply(
    var _id: String,
    var replier: String, // id
    var questionId: String,
    var images: ArrayList<Image>,
    var content: String,
    var createdTime: String = "01/01/2003" // dd/mm/yyyy
) : Parcelable
