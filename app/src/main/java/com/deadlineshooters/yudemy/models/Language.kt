package com.deadlineshooters.yudemy.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(
    var _id: String = "",
    var name: String = ""
) : Parcelable
