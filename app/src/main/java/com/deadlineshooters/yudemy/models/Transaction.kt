package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    @DocumentId
    var id: String = "",
    var senderId: String,
    var receiverId: String,
    var courseId: String,
    var amount: Number,
    var date: String
) : Parcelable
