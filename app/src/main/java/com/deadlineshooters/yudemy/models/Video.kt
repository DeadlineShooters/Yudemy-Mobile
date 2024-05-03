package com.deadlineshooters.yudemy.models

import android.net.Uri
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    override var secure_url: String = "",
    override var public_id: String = "",
    var resource_type: String = "",
    var duration: Double = 0.0, // in seconds
    @get:Exclude
    var contentUri: Uri? = null
) : Media(secure_url, public_id)
