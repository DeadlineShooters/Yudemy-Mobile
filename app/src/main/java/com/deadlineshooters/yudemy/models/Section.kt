package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Section(
    @DocumentId
    var _id: String = "",
    var title: String = "",
    var index: Int = 0,
) : Parcelable