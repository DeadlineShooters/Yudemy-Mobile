package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Section(
    val _id: String = "",
    val title: String = "",
    val index: Int = 0,

) : Parcelable