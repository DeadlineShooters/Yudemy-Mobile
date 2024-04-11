package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Video
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CourseRepository {
    private val userRepository = UserRepository()
    private val mFireStore = FirebaseFirestore.getInstance()
    private val coursesCollection = mFireStore.collection("courses")
    private val auth = FirebaseAuth.getInstance()


    fun generateDummyCourse(img: Image, vid: Video): Course {
        return Course(
            name = "Graph Theory Algorithms for Competitive Programming (2022)",
            instructor = "34349",
            totalStudents = 0,
            introduction = "Learn Graphs Algorithms in Computer Science & Mathematics, theory + hands-on coding and ace Competitive Coding problems!",
            description = "Welcome to Graph Algorithms for Competitive Coding - the most detailed Specialisation in Graph Theory for Competitive Programmers, Software Engineers & Computer Science students!\n" +
                    "\n" +
                    "\n" +
                    "Graphs is quite an important topic for software engineers, both for academics & online competitions and for solving real life challenges. Graph algorithms form the very fundamentals of many popular applications like - Google Maps, social media apps like Facebook, Instagram, Quora, LinkedIn, Computer Vision applications such as image segmentation, resolving dependencies while compile time, vehicle routing problems in supply chain and many more. This course provides a detailed overview of Graph Theory algorithms in computer science, along with hands on implementation of all the algorithms in C++. Not just that you will get 80+ competitive coding questions, to practice & test your skills! \n" +
                    "\n" +
                    "This comprehensive course is taught by Prateek Narang & Apaar Kamal, who are Software Engineers at Google and have taught over thousands of students in competitive programming over last 5+ years. This course is worth thousands of dollars, but Coding Minutes is providing you this course to you at a fraction of its original cost! This is action oriented course, we not just delve into theory but focus on the practical aspects by building implementing algorithms & solving problems. With over 95+ high quality video lectures, easy to understand explanations this is one of the most detailed and robust course for Graph Algorithms ever created.\n" +
                    "\n" +
                    "Course starts very basics with how to store and represent graphs on a computer, and then dives into popular algorithms & techniques for problem solving. The course is divided into two parts.",
            price = 1499000.0,
            promotionalVideo = vid,
            language = "ylTlDABgESXAzOHGyAxR", // English
            category = "hJqfxq5tTYVFsw69Mts9",
            thumbnail = img
        )
    }

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