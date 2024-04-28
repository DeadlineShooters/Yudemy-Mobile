package com.deadlineshooters.yudemy.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class CourseFeedback(
    @DocumentId var id: String = "",
    var courseId: String = "",
    var userId: String = "",
    var feedback: String = "",
    var rating: Int = 5,
    var createdDatetime: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),  // last updated
    var instructorResponse: FeedbackResponse? = null,
    var course: Course? = null
) : Parcelable
