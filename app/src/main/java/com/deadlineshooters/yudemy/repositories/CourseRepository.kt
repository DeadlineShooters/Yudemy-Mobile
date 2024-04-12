package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CourseRepository {
    private val userRepository = UserRepository()
    private val mFireStore = FirebaseFirestore.getInstance()
    private val coursesCollection = mFireStore.collection("courses")
    private val auth = FirebaseAuth.getInstance()

    fun addCourse(course: Course) {
        val documentReference = coursesCollection.document()
        course.id = documentReference.id
        documentReference.set(course)
            .addOnSuccessListener {
                Log.d("Course", "DocumentSnapshot successfully written!\n$course")
            }
            .addOnFailureListener { e ->
                Log.w("Course", "Error writing document", e)
            }
    }

    fun getCoursesByInstructor(instructorId: String? = null, sortByNewest: Boolean = true): Task<List<Course>> {
        val task = if (instructorId != null) {
            coursesCollection.whereEqualTo("instructor", instructorId).get()
        } else {
            coursesCollection.get()
        }

        return task.continueWith { task ->
            if (task.isSuccessful) {
                val result = task.result
                val courses = result?.map { document ->
                    val course = document.toObject(Course::class.java)
                    course.id = document.id
                    course 
                } ?: emptyList()

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                courses.sortedWith(compareBy { sdf.parse(it.createdDate) }).let { if (sortByNewest) it.reversed() else it }
            } else {
                emptyList()
            }
        }
    }
    fun getCourses(callback: (List<Course>) -> Unit) {
        val courses = mutableListOf<Course>()

        coursesCollection.get().addOnSuccessListener { courseDocument ->
            if (courseDocument != null) {
                var fetchedCourses = 0
                for (document in courseDocument) {
                    val course = document.toObject(Course::class.java)
                    course.id = document.id
                    userRepository.getUser(course.instructor) { user ->
                        course.instructor = user.fullName
                        courses.add(course)
                        fetchedCourses++
                        if (fetchedCourses == courseDocument.size()) {
                            callback(courses)
                        }
                    }
                }
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "get failed with ", exception)
        }
    }


    fun getWishlist(callback: (List<Course>) -> Unit) {
        val courses = mutableListOf<Course>()

        userRepository.getWishlistID { wishlistID ->
            var fetchedCourses = 0
            for (courseId in wishlistID) {
                coursesCollection.document(courseId).get().addOnSuccessListener { courseDocument ->
                    if (courseDocument != null) {
                        val course = courseDocument.toObject(Course::class.java)!!
                        course.id = courseDocument.id
                        userRepository.getUser(course.instructor) { user ->
                            course.instructor = user.fullName
                            courses.add(course)
                            fetchedCourses++
                            if (fetchedCourses == wishlistID.size) {
                                callback(courses)// Pass the courses to the callback function
                            }
                        }
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
            }
        }
    }

    fun patchCourse(course: Course) {
        val courseDocument = coursesCollection.document(course.id)

        val updates = hashMapOf<String, Any>(
            "name" to course.name,
            "introduction" to course.introduction,
            "description" to course.description,
            "thumbnail" to course.thumbnail // Make sure this is in a format Firestore can understand
        )

        courseDocument.update(updates)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating document", e)
            }
    }



    fun getCourseById(courseId: String, callback: (Course?) -> Unit) {
        var course: Course?
        coursesCollection
            .document(courseId)
            .get()
            .addOnSuccessListener { document ->
                course = document?.toObject(Course::class.java)
                callback(course)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                callback(null)
            }
    }

    fun searchCourses(input: String, callback: (List<Course>) -> Unit) {
        val courses = mutableListOf<Course>()

        coursesCollection.get().addOnSuccessListener { courseDocument ->
            if (courseDocument != null) {
                var fetchedCourses = 0
                for (document in courseDocument) {
                    val course = document.toObject(Course::class.java)

                    course.id = document.id
                    userRepository.getUser(course.instructor) { user ->
                        course.instructor = user.fullName
                        if (course.name.contains(input, ignoreCase = true)) {
                            courses.add(course)
                        }
                        fetchedCourses++
                        if (fetchedCourses == courseDocument.size()) {
                            callback(courses)
                        }
                    }
                }
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "get failed with ", exception)
        }

    }

}