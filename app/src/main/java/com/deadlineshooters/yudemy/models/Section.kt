package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Section(
    var _id: String = "",
    var title: String = "",
    var index: Int = 0,

) : Parcelable