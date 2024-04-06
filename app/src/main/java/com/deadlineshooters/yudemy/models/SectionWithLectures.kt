package com.deadlineshooters.yudemy.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class SectionWithLectures(
    val section: Section,
    val lectures: List<Lecture>
) : Parcelable