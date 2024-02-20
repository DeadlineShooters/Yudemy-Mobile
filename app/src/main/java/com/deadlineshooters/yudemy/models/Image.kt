package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val secure_url: String = "",
    val public_id: String = ""
) : Parcelable
