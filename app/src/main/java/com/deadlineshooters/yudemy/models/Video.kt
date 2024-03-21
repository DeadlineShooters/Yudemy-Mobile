package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
data class Video(
    override var secure_url: String = "",
    override var public_id: String = "",
    var resource_type: String = "",
    var duration: Double = 0.0 // in seconds
) : Media(secure_url, public_id)
