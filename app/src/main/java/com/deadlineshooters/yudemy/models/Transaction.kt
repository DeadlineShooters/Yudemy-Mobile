package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Transaction(
    var _id: String,
    var senderId: String,
    var receiverId: String,
    var courseId: String,
    var amount: Number,
    var date: String
) : Parcelable
