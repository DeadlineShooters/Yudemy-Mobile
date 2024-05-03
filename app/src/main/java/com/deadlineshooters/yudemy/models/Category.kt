package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @DocumentId
    var _id: String = "",
    var name: String = ""
) : Parcelable