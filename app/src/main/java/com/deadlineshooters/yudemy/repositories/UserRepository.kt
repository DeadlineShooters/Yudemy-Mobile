package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val userCollection = mFireStore.collection("users")

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
                    val listCourse = document.data?.get("courseList") as ArrayList<String>
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
                                    callback(courses as ArrayList<Map<Course, String>>, progresses as ArrayList<Number>)
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

    fun deleteUser(userId: String) {
        mFireStore.collection("users")
            .document(userId)
            .delete()
    }

    fun getReminderDays(callback: (ArrayList<Number>) -> Unit) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val listDays = document.data?.get("reminderDays") as ArrayList<Number>
                    callback(listDays)
                }
                else {
                    Log.d("Firestore", "No such document")
                }
            }
    }

    fun getReminderTimes(callback: (ArrayList<Number>) -> Unit) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val listTimes = document.data?.get("reminderTimes") as ArrayList<Number>
                    callback(listTimes)
                }
                else {
                    Log.d("Firestore", "No such document")
                }
            }
    }

    fun addReminderDay(day: Int) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .update("reminderDays", FieldValue.arrayUnion(day))
    }

    fun removeReminderDay(day: Int) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .update("reminderDays", FieldValue.arrayRemove(day))
    }

    fun addReminderTime(time: Int) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .update("reminderTimes", FieldValue.arrayUnion(time))
    }

    fun removeReminderTime(time: Int) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .update("reminderTimes", FieldValue.arrayRemove(time))
    }

    fun checkIfReminderToggled(callback: (Boolean) -> Unit) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val isToggleReminder = document.data?.get("reminderNotification") as Boolean
                    callback(isToggleReminder)
                }
                else {
                    Log.d("Firestore", "No such document")
                }
            }
    }

    fun toggleReminder(isToggle: Boolean, callback: (Boolean) -> Unit) {
        userCollection
            .document(mAuth.currentUser!!.uid)
            .update("reminderNotification", isToggle)
            .addOnSuccessListener {
                if(isToggle) {
                    getReminderDays { days ->
                        getReminderTimes { times ->
                            if(days.isEmpty() || times.isEmpty()) {
                                addReminderDay(0)
                                addReminderTime(0)
                                callback(true)
                            }
                            else {
                                callback(false)
                            }
                        }
                    }
                }
            }
    }
}