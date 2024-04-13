package com.deadlineshooters.yudemy.models

import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    override var secure_url: String = "",
    override var public_id: String = ""
) : Media(secure_url, public_id)
