package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.RawValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Certificate(
    @DocumentId
    var _id: String = "",
    var courseId: String = "",
    var createdDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    var length: Int = 0, // in seconds
    var title: String = "",
    var userId: String = ""
) : Parcelable