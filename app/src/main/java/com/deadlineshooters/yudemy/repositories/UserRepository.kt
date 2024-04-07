package com.deadlineshooters.yudemy.repositories

import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.activities.SignUpActivity
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    fun getUserData() : User{
        // for testing
        return User("Test")
    }
    fun addUser(user: User) {
        mFireStore.collection("users")
            .document(mAuth.currentUser!!.uid)
            .set(user)
    }

    /**
     * Get user courses with progress and instructor name
     *
     * @return A callback function that takes a list of courses and instructors and a list of progress
     */
    fun getUserCourses(callback: (ArrayList<Map<Course, String>>, ArrayList<Number>) -> Unit) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val data = document.data?.get("courseList")
                    val listCourse = if (data is ArrayList<*>) {
                        data as ArrayList<String>
                    } else {
                        // data is null or an empty list
                        ArrayList<String>()
                    }

                    val progressTasks: ArrayList<Task<Number>> = arrayListOf()
                    val tasks = listCourse.map { courseId ->
                        val task = TaskCompletionSource<Map<Course, String>>()
                        CourseRepository().getCourseById(courseId) { course ->
                            course?.instructor?.let {
                                InstructorRepository().getInstructorNameById(it) { name ->
                                    task.setResult(mapOf(course to (name ?: "")))
                                }
                            }
                        }
                        val progressTask = TaskCompletionSource<Number>()
                        CourseProgressRepository().getCourseProgressByCourse(courseId) { courseProgress ->
                            progressTask.setResult(courseProgress)
                        }
                        progressTasks.add(progressTask.task)
                        task.task
                    }
                    Tasks.whenAllSuccess<Map<Course, String>>(tasks)
                        .addOnSuccessListener { courses ->
                            Tasks.whenAllSuccess<Int>(progressTasks)
                                .addOnSuccessListener { progresses ->
                                    callback(ArrayList(courses), ArrayList(progresses))
                                }
                        }
                }
                else {
                    Log.d("Firestore", "No such document")
                }
            }
    }

    fun getUserFavoriteCourseIds(callback: (ArrayList<String>) -> Unit) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val listCourse = document.data?.get("favoriteCourses") as ArrayList<String>
                    callback(listCourse)
                }
                else {
                    Log.d("Firestore", "No such document")
                }
            }
    }
}