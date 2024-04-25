package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Question(
    @DocumentId
    var _id: String = "",
    var asker: String = "", // object id
    var lectureId: String = "",
    var title: String = "",
    var details: String = "",
    var images: ArrayList<Image> = ArrayList(),
    var createdTime: String = "" // dd/mm/yyyy
) : Parcelable