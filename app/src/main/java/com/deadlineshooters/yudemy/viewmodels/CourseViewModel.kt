package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Video
import com.deadlineshooters.yudemy.repositories.CourseRepository


class CourseViewModel : ViewModel() {
    private val courseRepository = CourseRepository()
    private val cloudinaryHelper = CloudinaryHelper()
    fun addDummyCourse() {

        val imageFilePath = "/storage/emulated/0/Android/data/com.deadlineshooters.yudemy/files/DCIM/img_thumbnail.jpg"
        val videoFilePath = "/storage/emulated/0/Android/data/com.deadlineshooters.yudemy/files/DCIM/2 - Thanks.mp4"

        // Upload image
        cloudinaryHelper.uploadMedia(imageFilePath) { img ->
            // Handle the uploaded image
            // Upload video
            cloudinaryHelper.uploadMedia(videoFilePath, true) { vid ->
                // Handle the uploaded video
                // Generate dummy course and add it to the repository
                val dummyCourse = courseRepository.generateDummyCourse(img as Image, vid as Video)
                courseRepository.addCourse(dummyCourse)
            }
        }

    }
}