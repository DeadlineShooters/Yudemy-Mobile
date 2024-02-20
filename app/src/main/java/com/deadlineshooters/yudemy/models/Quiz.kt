package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quiz(
    val _id: String,
    val sectionId: String,
    val questions: ArrayList<Question>,
    val description: String,
    val choices: ArrayList<String>,
    val index: Int,
    val completed: Boolean


    ) : Parcelable