package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Transaction(
    var _id: String,
    var senderId: String,
    var receiverId: String,
    var courseId: String,
    var amount: Number,
    var date: @RawValue DateTime
) : Parcelable
