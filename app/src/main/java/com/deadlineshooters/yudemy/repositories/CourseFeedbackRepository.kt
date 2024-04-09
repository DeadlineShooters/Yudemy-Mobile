package com.deadlineshooters.yudemy.repositories

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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

    fun saveFeedback(oldRating : CourseFeedback?, course: Course, feedback: CourseFeedback, callback: FeedbackCallback) {
        // If the feedback doesn't have an id, create a new document and get its ID
        val feedbackRef = if (oldRating == null) {
            val newDoc = feedbackCollection.document()

            // update rating
            course.totalRatings += 1
            course.avgRating = (course.avgRating * (course.totalRatings - 1) + feedback.rating.toDouble()) / course.totalRatings
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
            course.avgRating = ((course.avgRating * course.totalRatings) - oldRating.rating + feedback.rating.toDouble()) / course.totalRatings
            feedbackCollection.document(oldRating.id)
        }

        // Set the feedback to the document, creating it if it doesn't exist or updating it if it does
        feedbackRef.set(feedback)
            .addOnSuccessListener {
                // Get a reference to the course document
                val courseRef = mFireStore.collection(Constants.COURSES).document(course.id)

                // Update the course document
                courseRef.update(
                    "totalRatings", course.totalRatings,
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




    fun getCourseFeedback(courseId: String, callback: (List<CourseFeedback>) -> Unit) {
        feedbackCollection.whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { documents ->
                val feedbackList = documents.map { it.toObject(CourseFeedback::class.java) }
                callback(feedbackList)
            }
            .addOnFailureListener { e ->
                Log.w(this.javaClass.simpleName, "Error getting documents: ", e)
                callback(emptyList())
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

    fun getLatestCourseFeedback(courseId: String, context: Context, callback: (List<CourseFeedback>?) -> Unit) {
        feedbackCollection.whereEqualTo("courseId", courseId)
            .orderBy("createdDatetime", Query.Direction.DESCENDING)
            .limit(2)
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
}