package com.deadlineshooters.yudemy.models
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class User(
    var fullName: String = "",
//    var avatar: Image = Image(),
    var courseList: ArrayList<String> = arrayListOf(),
    var wishList: ArrayList<String> = arrayListOf(),
    var favoriteCourses: ArrayList<String> = arrayListOf(),
    var reminderDays: ArrayList<Int> = arrayListOf(),
    var reminderTimes: ArrayList<Int> = arrayListOf(),
    var reminderNotification: Boolean = false,
    var fcmToken: String = "",
    var instructor: Instructor? = null
) : Parcelable{
    @get:Exclude var id: String = ""
}