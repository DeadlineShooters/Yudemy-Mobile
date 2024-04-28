package com.deadlineshooters.yudemy.repositories

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.models.FeedbackResponse
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface FeedbackCallback {
    fun onSuccess()
    fun onFailure(e: Exception)
}

interface FeedbackUpdateListener {
    fun onFeedbackUpdated(feedback: CourseFeedback)
}

class CourseFeedbackRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val feedbackCollection = mFireStore.collection(Constants.COURSE_FEEDBACK)
    private val usersCollection = mFireStore.collection(Constants.USERS)
    private val coursesCollection = mFireStore.collection(Constants.COURSES)


    fun saveFeedback(oldRating : CourseFeedback?, course: Course, feedback: CourseFeedback, callback: FeedbackCallback) {
        var totalRatings = course.fiveStarCnt + course.fourStarCnt + course.threeStarCnt + course.twoStarCnt + course.oneStarCnt

        // If the feedback doesn't have an id, create a new document and get its ID
        val feedbackRef = if (oldRating == null) {
            val newDoc = feedbackCollection.document()

            totalRatings += 1
            course.avgRating = (course.avgRating * (totalRatings - 1) + feedback.rating.toDouble()) / totalRatings

            // increment the count of the corresponding star rating
            when (feedback.rating) {
                5 -> course.fiveStarCnt += 1
                4 -> course.fourStarCnt += 1
                3 -> course.threeStarCnt += 1
                2 -> course.twoStarCnt += 1
                1 -> course.oneStarCnt += 1
            }
            newDoc

        } else {
            // Otherwise, get a reference to the existing document
            course.avgRating = ((course.avgRating * totalRatings) - oldRating.rating + feedback.rating.toDouble()) / totalRatings
            feedbackCollection.document(oldRating.id)
        }

        // Set the feedback to the document, creating it if it doesn't exist or updating it if it does
        feedbackRef.set(feedback)
            .addOnSuccessListener {
                // Get a reference to the course document
                val courseRef = mFireStore.collection(Constants.COURSES).document(course.id)

                // Update the course document
                courseRef.update(
                    "avgRating", course.avgRating,
                    "fiveStarCnt", course.fiveStarCnt,
                    "fourStarCnt", course.fourStarCnt,
                    "threeStarCnt", course.threeStarCnt,
                    "twoStarCnt", course.twoStarCnt,
                    "oneStarCnt", course.oneStarCnt
                )
                    .addOnSuccessListener {
                        callback.onSuccess()
                    }
                    .addOnFailureListener { e ->
                        callback.onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                callback.onFailure(e)
            }
    }



    fun getFeedbackForCourseAndUser(courseId: String, userId: String, callback: (CourseFeedback?) -> Unit) {
        feedbackCollection
            .whereEqualTo("courseId", courseId)
            .whereEqualTo("userId", userId)
            .limit(1)  // We only expect at most one document because a user can only leave one feedback for a course
            .get()
            .addOnSuccessListener { documents ->
                val feedback = if (documents.isEmpty) null else documents.documents[0].toObject(CourseFeedback::class.java)
                callback(feedback)
            }
            .addOnFailureListener { e ->
                Log.e(this.javaClass.simpleName, "Can't get feedback for course and user: $e")
            }
    }

    fun getLatestCourseFeedback(courseId: String, callback: (List<CourseFeedback>?) -> Unit) {
        feedbackCollection.whereEqualTo("courseId", courseId)
            .orderBy("createdDatetime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val feedbackList = documents.map { it.toObject(CourseFeedback::class.java) }
                callback(feedbackList)
            }
            .addOnFailureListener { e ->
                Log.e(this.javaClass.simpleName, "Can't get latest course feedback: $e")

            }
    }

    fun getUserFullName(userId: String, callback: (String) -> Unit) {
        usersCollection.document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject(User::class.java)
                callback(user?.fullName ?: "")
            } else {
                callback("")
            }
        }
    }

    fun getAllInstructorFeedback(
        userId: String,
        noInstructorResponse: Boolean?
    ): Task<List<CourseFeedback>> {
        val taskCompletionSource = TaskCompletionSource<List<CourseFeedback>>()

        // First, get all courses created by the instructor
        coursesCollection.whereEqualTo("instructor", userId)
            .get()
            .addOnSuccessListener { documents ->
                val courses =
                    documents.map { it.toObject(Course::class.java) } // Extract course objects
                val courseMap =
                    courses.associateBy { it.id } // Create a map with courseId as key and Course as value

                // Then, get all feedback for these courses
                feedbackCollection.whereIn("courseId", courses.map { it.id })
                    .orderBy("createdDatetime", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { feedbackDocuments ->
                        var feedbackList = feedbackDocuments.map { document ->
                            val feedback = document.toObject(CourseFeedback::class.java)
                            feedback.course = courseMap[feedback.courseId]
                            feedback
                        }
                        // Filter feedback by instructor response
                        if (noInstructorResponse != null) {
                            feedbackList = if (noInstructorResponse) {
                                feedbackList.filter { it.instructorResponse == null }
                            } else {
                                feedbackList.filter { it.instructorResponse != null }
                            }
                        }
                        taskCompletionSource.setResult(feedbackList)
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            this.javaClass.simpleName,
                            "Can't get instructor's course feedback: $e"
                        )
                        taskCompletionSource.setException(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(this.javaClass.simpleName, "Can't get instructor's courses: $e")
                taskCompletionSource.setException(e)
            }

        return taskCompletionSource.task
    }


    fun saveFeedbackResponse(instructorId: String, feedbackId: String, responseText: String, callback: (Boolean, CourseFeedback?) -> Unit) {
        val feedbackResponse = FeedbackResponse(
            instructorId = instructorId, // replace with the actual instructor id
            content = responseText,
            createdDatetime = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )

        feedbackCollection.document(feedbackId)
            .update("instructorResponse", feedbackResponse)
            .addOnSuccessListener {
                Log.d(this.javaClass.simpleName, "Feedback response saved successfully.")

                feedbackCollection.document(feedbackId).get()
                    .addOnSuccessListener { document ->
                        val courseFeedback = document.toObject(CourseFeedback::class.java)
                        callback(true, courseFeedback)
                    }
                    .addOnFailureListener { e ->
                        Log.e(this.javaClass.simpleName, "Can't get updated feedback: $e")
                        callback(false, null)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(this.javaClass.simpleName, "Can't save feedback response: $e")
                callback(false, null)
            }
    }




}