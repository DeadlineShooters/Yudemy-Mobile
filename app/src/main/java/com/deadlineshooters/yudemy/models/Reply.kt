package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import java.util.*
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Reply(
    @DocumentId
    var _id: String = "",
    var replier: String = "", // id
    var questionId: String = "",
    var images: ArrayList<Image> = arrayListOf(),
    var content: String = "",
    var createdTime: String = "01/01/2003" // dd/mm/yyyy
) : Parcelable
