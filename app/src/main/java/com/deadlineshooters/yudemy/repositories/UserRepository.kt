package com.deadlineshooters.yudemy.repositories

import android.content.ContentValues
import android.util.Log
import com.deadlineshooters.yudemy.activities.CourseDetailActivity
import com.deadlineshooters.yudemy.fragments.AccountFragment
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val usersCollection = mFireStore.collection(Constants.USERS)

    /**
     * Load user data onto any page
     * */

    fun getUserData() : User{
        // for testing
        return User("Test")
    }
    fun loadUserData(context: Any) {
        val documentReference = usersCollection.document(getCurrentUserID())

        documentReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(this.javaClass.simpleName, "DocumentSnapshot data: ${document.data}")
                    val loggedInUser = document.toObject(User::class.java)!!

                    when (context) {
                        is AccountFragment -> {
                            context.setUserData(loggedInUser)
                        }
                    }

                } else {
                    Log.d(this.javaClass.simpleName, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(this.javaClass.simpleName, "get failed with ", exception)
            }
    }

    fun loadInstructorData(context: Any, instructorId: String) {
        val documentReference = usersCollection.document(instructorId)

        documentReference.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d(this.javaClass.simpleName, "DocumentSnapshot data: ${document.data}")
                    val instructor = document.toObject(User::class.java)
                    if (instructor != null) {
                        when (context) {
                            is CourseDetailActivity -> {
                                context.setInstructor(instructor)
                            }
                        }
                    } else {
                        Log.d(this.javaClass.simpleName, "Failed to convert document to User")
                    }
                } else {
                    Log.d(this.javaClass.simpleName, "No such document")
                }
            }
    }


    fun becomeInstructor() {
        val userRef = usersCollection.document(getCurrentUserID())

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d(this.javaClass.simpleName, "DocumentSnapshot data: ${document.data}")

                // Check if the instructor field is null
                if (document.get("instructor") == null) {
                    val newInstructor = Instructor()

                    userRef.update("instructor", newInstructor)
                        .addOnSuccessListener {
                            Log.d(
                                this.javaClass.simpleName,
                                "DocumentSnapshot successfully updated!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                this.javaClass.simpleName,
                                "Error updating document",
                                e
                            )
                        }
                }
            } else {
                Log.d(this.javaClass.simpleName, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(this.javaClass.simpleName, "get failed with ", exception)
            }
    }

    fun addUser(user: User) {
        usersCollection
            .document(mAuth.currentUser!!.uid)
            .set(user)
    }

    fun getCurUser(callbacks: (User) -> Unit){
        mFireStore.collection("users")
            .document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    callbacks(document.toObject(User::class.java)!!)
                }
            }
    }

    fun getUserById(userId: String, callbacks: (User?) -> Unit){
        mFireStore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    callbacks(document.toObject(User::class.java)!!)
                }
                else{
                    callbacks(null)
                }
            }
            .addOnFailureListener{
                callbacks(null)
            }
    }

    fun updateUserImage(userId: String, image: Image, callbacks: (User) -> Unit){
        usersCollection.document(userId).update(
            "image.public_id", image.public_id,
            "image.secure_url", image.secure_url
        ).addOnSuccessListener {
            getUserById(userId){
                callbacks(it!!)
            }
        }.addOnFailureListener {
            Log.w(ContentValues.TAG, "Error updating document", it)
        }
    }

    companion object {
        fun getCurrentUserID(): String {
            return FirebaseAuth.getInstance().currentUser!!.uid.toString()
        }
    }

    fun getWishlistID(callback: (List<String>) -> Unit) {
        val uid = mAuth.currentUser?.uid
        usersCollection
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
        usersCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as MutableList<String>
                    if (!wishlist.contains(courseId)) {
                        wishlist.add(courseId)
                        usersCollection.document(uid).update("wishList", wishlist)
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
        usersCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val wishlist = document.get("wishList") as MutableList<String>
                    if (wishlist.contains(courseId)) {
                        wishlist.remove(courseId)
                        usersCollection.document(uid).update("wishList", wishlist)
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
        usersCollection
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
        usersCollection
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val courseList = document.get("courseList") as MutableList<String>
                    if (!courseList.contains(courseId)) {
                        courseList.add(courseId)
                        usersCollection.document(uid).update("courseList", courseList)
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

    /**
     * Get user courses with progress and instructor name
     *
     * @return A callback function that takes a list of courses and instructors and a list of progress
     */
    fun getUserCourses(callback: (ArrayList<Map<Course, String>>, ArrayList<Number>) -> Unit) {
        usersCollection
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
        usersCollection
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
}