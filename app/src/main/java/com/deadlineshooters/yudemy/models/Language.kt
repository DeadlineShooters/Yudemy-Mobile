package com.deadlineshooters.yudemy.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(
    val _id: String = "",
    val name: String = ""
) : Parcelable
