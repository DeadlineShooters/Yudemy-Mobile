package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Certificate(
    var _id: String = "",
    var createdDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),

    var courseId: String = "",
    var title: String = "",
    var length: Int = 0 // in seconds
) : Parcelable