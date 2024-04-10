package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Question
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class QuestionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val questionsCollection = mFireStore.collection("questions")

    fun getQuestionListByCourseId(courseId: String, callback: (ArrayList<Question>) -> Unit){
        mFireStore.collection("courses")
            .document(courseId)
            .get()
            .addOnSuccessListener { result ->
                val sectionList: ArrayList<String> =
                    result.data?.get("sectionList") as ArrayList<String>
                val questionTasks = sectionList.map { section ->
                    val task = TaskCompletionSource<ArrayList<Question>>()
                    mFireStore.collection("lectures").whereEqualTo("sectionId", section).get()
                        .addOnSuccessListener { lectureList ->
                            for (lecture in lectureList) {
                                val lectureId = lecture.id
                                getQuestionListByLectureId(lectureId){
                                    task.setResult(it)
                                }
                            }
                        }
                    task.task
                }
                Tasks.whenAllSuccess<ArrayList<Question>>(questionTasks)
                    .addOnSuccessListener {
                        callback(it.flatten() as ArrayList<Question>)
                    }
            }
        .addOnFailureListener { exception ->
            callback(ArrayList())
        }
    }


    private fun getQuestionListByLectureId(lectureId: String, callback: (ArrayList<Question>) -> Unit){
        questionsCollection.whereEqualTo("lectureId", lectureId)
            .get()
            .addOnSuccessListener { result ->
                val list: ArrayList<Question> = ArrayList()
                for (document in result) {
                    val question = document.toObject(Question::class.java)
                    list.add(question)
                }
                callback(list)
            }
            .addOnFailureListener { exception ->
                callback(ArrayList())
            }
    }

    fun addNewQuestion(courseId: String,asker: String, title: String, details: String, imageArray: ArrayList<Image>, lectureId: String, createdTime: String, callback: (ArrayList<Question>) -> Unit){
        val question = Question(asker = asker, title = title, details = details, images = imageArray, lectureId = lectureId, createdTime = createdTime)
        questionsCollection.add(question)
        .addOnSuccessListener {
            getQuestionListByCourseId(courseId){
                callback(it)
            }
        }
        .addOnFailureListener {
            callback(ArrayList())
        }
    }

    fun getQuestionById(questionId: String, callback: (Question) -> Unit){
        questionsCollection.document(questionId)
        .get()
        .addOnSuccessListener { document ->
            val question = document.toObject(Question::class.java)
            if (question != null) {
                callback(question)
            }
        }
        .addOnFailureListener {
            callback(Question())
        }
    }


    fun editQuestion(questionId: String, asker: String, title: String, details: String, imageArray: ArrayList<Image>, lectureId: String, createdTime: String, callback: (Question) -> Unit){
        val question = Question(asker = asker, title = title, details = details, images = imageArray, lectureId = lectureId, createdTime = createdTime)
        questionsCollection.document(questionId).set(question)
        .addOnSuccessListener {
            getQuestionById(questionId){
                callback(it)
            }
        }
        .addOnFailureListener {
            callback(Question())
        }
    }

    fun deleteQuestion(courseId: String, questionId: String, callback: (ArrayList<Question>) -> Unit){
        questionsCollection.document(questionId).delete()
        .addOnSuccessListener {
            getQuestionListByCourseId(courseId){
                callback(it)
            }
        }
        .addOnFailureListener {
            callback(ArrayList())
        }
    }
}