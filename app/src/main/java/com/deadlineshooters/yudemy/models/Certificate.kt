package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.type.Date
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Certificate(
    var _id: String = "",
    var createdDate: @RawValue Date? = null,
    var courseId: String = "",
    var title: String = "",
    var length: Int = 0 // in seconds
) : Parcelable