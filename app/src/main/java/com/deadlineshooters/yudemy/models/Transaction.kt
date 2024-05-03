package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Transaction(
    @DocumentId
    var id: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var courseId: String = "",
    var amount: Double  = 0.0,
    var date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
) : Parcelable
