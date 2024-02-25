package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quiz(
    var _id: String,
    var sectionId: String,
    var questions: ArrayList<Question>,
    var description: String,
    var choices: ArrayList<String>,
    var index: Int,
    var completed: Boolean


    ) : Parcelable