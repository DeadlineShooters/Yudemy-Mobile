package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizQuestion(
    val _id: String = "",
    val answers: ArrayList<String> = arrayListOf(),
    val bestAnswer: Int = 0,

    ) : Parcelable