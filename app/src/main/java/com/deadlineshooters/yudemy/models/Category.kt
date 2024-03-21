package com.deadlineshooters.yudemy.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Category(
    var _id: String = "",
    var name: String = ""
) : Parcelable