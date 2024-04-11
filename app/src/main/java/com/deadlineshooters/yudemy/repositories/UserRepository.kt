package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
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
        userCollection
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

    fun getUser(userId: String, callback: (User) -> Unit) {
        userCollection
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)!!
                    callback(user)
                } else {
                    Log.d("Firestore", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }



    fun getWishlistID(callback: (List<String>) -> Unit) {
        val uid = mAuth.currentUser?.uid
        userCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as List<String>
                    callback(wishlist) // Pass the wishlist to the callback function
                } else {
                    Log.d("Firestore", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }

    fun addToWishlist(courseId: String, callback: (Boolean) -> Unit) {
        val uid = mAuth.currentUser?.uid
        userCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as MutableList<String>
                    if (!wishlist.contains(courseId)) {
                        wishlist.add(courseId)
                        userCollection.document(uid).update("wishList", wishlist)
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Firestore", "update failed with ", exception)
                                callback(false)
                            }
                    } else {
                        callback(false)
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
                callback(false)
            }
    }

    fun removeFromWishlist(courseId: String, callback: (Boolean) -> Unit) {
        val uid = mAuth.currentUser?.uid
        userCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as MutableList<String>
                    if (wishlist.contains(courseId)) {
                        wishlist.remove(courseId)
                        userCollection.document(uid).update("wishList", wishlist)
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Firestore", "update failed with ", exception)
                                callback(false)
                            }
                    } else {
                        callback(false)
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
                callback(false)
            }
    }

    fun isInWishlist(courseId: String, callback: (Boolean) -> Unit) {
        val uid = mAuth.currentUser?.uid
        userCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as MutableList<String>
                    if (wishlist.contains(courseId)) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
                callback(false)
            }
    }

    fun addToCourseList(courseId: String, callback: (Boolean) -> Unit) {
        val uid = mAuth.currentUser?.uid
        userCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val courseList = document.get("courseList") as MutableList<String>
                    if (!courseList.contains(courseId)) {
                        courseList.add(courseId)
                        userCollection.document(uid).update("courseList", courseList)
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Firestore", "update failed with ", exception)
                                callback(false)
                            }
                    } else {
                        callback(false)
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
                callback(false)
            }
    }

}