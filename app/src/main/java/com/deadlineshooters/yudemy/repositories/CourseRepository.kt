package com.deadlineshooters.yudemy.repositories

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.utils.Constants
import com.deadlineshooters.yudemy.models.Video
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CourseRepository {
    private val userRepository = UserRepository()
    private val categoryRepository = CategoryRepository()
    private val languageRepository = LanguageRepository()
    private val mFireStore = FirebaseFirestore.getInstance()
    private val coursesCollection = mFireStore.collection(Constants.COURSES)
    private val usersCollection = mFireStore.collection("users")
    private val auth = FirebaseAuth.getInstance()

    fun addCourse(course: Course): Task<String> {
        course.instructor = auth.currentUser?.uid.toString()
        return coursesCollection
            .add(course)
            .continueWith { task ->
                task.result.id
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
                    userRepository.getUserById(course.instructor) { user ->

                        categoryRepository.getCategory(course.category) { category ->
                            course.category = category.name
                            languageRepository.getLanguage(course.language) { language ->
                                course.language = language.name
                                if (course.status) {
                                    courses.add(course)
                                }
                                fetchedCourses++
                                if (fetchedCourses == courseDocument.size()) {
                                    callback(courses)
                                }
                            }
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
            if (wishlistID.isEmpty()) {
                callback(courses)
                return@getWishlistID
            }

            var fetchedCourses = 0
            for (courseId in wishlistID) {
                coursesCollection.document(courseId).get().addOnSuccessListener { courseDocument ->
                    if (courseDocument != null && courseDocument.exists()) {
                        val course = courseDocument.toObject(Course::class.java)
                        if (course != null) {
                            course.id = courseDocument.id
                            userRepository.getUserById(course.instructor) { user ->
                                if (user != null) {
                                    course.instructor = user.fullName
                                }
                                courses.add(course)
                                fetchedCourses++
                                if (fetchedCourses == wishlistID.size) {
                                    callback(courses)
                                }
                            }
                        } else {
                            Log.d("Firestore", "Failed to convert document to Course")
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


    fun patchCourse(course: Course, onSuccess: (() -> Unit)? = null) {
        val courseDocument = coursesCollection.document(course.id)

        val updates = hashMapOf<String, Any>(
            "name" to course.name,
            "category" to course.category,
            "introduction" to course.introduction,
            "description" to course.description,
            "thumbnail" to course.thumbnail,
            "promotionalVideo" to course.promotionalVideo
        )

        courseDocument.update(updates)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully updated!")
                onSuccess?.invoke()  // Invoke the success callback if it's not null
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating document", e)
            }
    }

    fun getCourses(): LiveData<List<Course>> {
        val coursesLiveData = MutableLiveData<List<Course>>()

        coursesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val courses = snapshot.documents.mapNotNull { it.toObject(Course::class.java) }
                coursesLiveData.value = courses
            } else {
                Log.d(TAG, "Current data: null")
            }
        }

        return coursesLiveData
    }

    fun getTop3InstructorCourseList(instructorId: String, callbacks: (List<Course>) -> Unit) {
        coursesCollection.whereEqualTo("instructor", instructorId).limit(3).get()
            .addOnSuccessListener { documents ->
                val courses = documents.mapNotNull { it.toObject(Course::class.java) }
                callbacks(courses)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                callbacks(emptyList())
            }
    }

    fun getInstructorCourseList(instructorId: String, callbacks: (List<Course>) -> Unit) {
        coursesCollection.whereEqualTo("instructor", instructorId).get()
            .addOnSuccessListener { documents ->
                val courses = documents.mapNotNull { it.toObject(Course::class.java) }
                callbacks(courses)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                callbacks(emptyList())
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

    fun updateCourseStats(courseId: String, callback: (Boolean) -> Unit) {
        coursesCollection
            .document(courseId)
            .get()
            .addOnSuccessListener { document ->
                val course = document?.toObject(Course::class.java)
                if (course != null) {
                    course.totalStudents++
                    course.totalRevenue += course.price

                    coursesCollection.document(courseId).set(course)
                        .addOnSuccessListener {
                            usersCollection.document(course.instructor).get()
                                .addOnSuccessListener { userDocument ->
                                    val user = userDocument?.toObject(User::class.java)
                                    if (user != null) {
                                        user.instructor!!.totalStudents++

                                        usersCollection.document(course.instructor).set(user)
                                            .addOnSuccessListener {
                                                callback(true)
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.w("Firestore", "Error updating user document: ", exception)
                                                callback(false)
                                            }
                                    } else {
                                        callback(false)
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("Firestore", "Error getting user document: ", exception)
                                    callback(false)
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("Firestore", "Error updating course document: ", exception)
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting course document: ", exception)
                callback(false)
            }
    }


    fun addASection(courseId: String, section: String): Task<Void> {
        return coursesCollection.document(courseId)
            .update("sectionList", FieldValue.arrayUnion(section))
    }

    fun searchCourses(input: String, callback: (List<Course>) -> Unit) {
        val courses = mutableListOf<Course>()

        coursesCollection.get().addOnSuccessListener { courseDocument ->
            if (courseDocument != null) {
                var fetchedCourses = 0
                for (document in courseDocument) {
                    val course = document.toObject(Course::class.java)

                    course.id = document.id
                    userRepository.getUserById(course.instructor) { user ->
                        if (user != null) {
                            course.instructor = user.fullName
                        }
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

    fun updatePrice(courseId: String, price: Int): Task<Void> {
        return coursesCollection.document(courseId)
            .update("price", price)
    }

    /**
     * delete its sections, lectures, learner's progress, learner's course, learner's favorite course
     */
    fun deleteCourse(course: Course): Task<Void> {
        val sections = course.sectionList

        val tasks = mutableListOf<Task<*>>()
        for (section in sections) {
            val task = LectureRepository().getLecturesBySectionId(section)
                .continueWithTask {
                    val lectures = it.result
                    LectureRepository().deleteLectures(lectures, course)
                        .continueWithTask {
                            SectionRepository().deleteSection(section)
                        }
                }
            tasks.add(task)
        }

        return Tasks.whenAllComplete(tasks)
            .continueWithTask {
                coursesCollection.document(course.id).delete()
                    .continueWithTask {
                        CourseProgressRepository().deleteProgressByCourse(course.id)
                            .continueWithTask {
                                UserRepository().removeCourse(course.id)
                            }
                    }
            }
    }

    fun updateCourseStatus(courseId: String, status: Boolean): Task<Void> {
        return coursesCollection.document(courseId)
            .update("status", status)
    }

    fun updateTotal(lecture: Lecture, course: Course, isAdded: Boolean): Task<Void> {
        return coursesCollection.document(course.id)
            .update("totalLecture", FieldValue.increment(if (isAdded) 1 else -1))
            .continueWithTask {
                course.totalLecture += if (isAdded) 1 else -1
                coursesCollection.document(course.id)
                    .update(
                        "totalLength",
                        FieldValue.increment(if (isAdded) lecture.content.duration.toLong() else -lecture.content.duration.toLong())
                    )
                    .continueWithTask {
                        course.totalLength += if (isAdded) lecture.content.duration.toInt() else -lecture.content.duration.toInt()
                        it
                    }
            }
    }

    fun updateTotalLength(course: Course, oldLength: Int, newLength: Int): Task<Void> {
        return coursesCollection.document(course.id)
            .update("totalLength", FieldValue.increment(newLength.toLong() - oldLength.toLong()))
            .continueWithTask {
                course.totalLength += (newLength - oldLength)
                it
            }
    }

    fun removeSection(courseId: String, sectionId: String): Task<Void> {
        return coursesCollection.document(courseId)
            .update("sectionList", FieldValue.arrayRemove(sectionId))
    }
}