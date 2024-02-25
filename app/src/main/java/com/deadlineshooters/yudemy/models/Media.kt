package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Media(
    open var secure_url: String = "",
    open var public_id: String = ""
) : Parcelable

