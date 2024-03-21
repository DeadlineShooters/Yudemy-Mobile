package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
data class QuizQuestion(
    var _id: String = "",
    var answers: ArrayList<String> = arrayListOf(),
    var bestAnswer: Int = 0,

    ) : Parcelable