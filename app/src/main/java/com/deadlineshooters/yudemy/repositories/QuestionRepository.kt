package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
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
                            var lectureTasks = mutableListOf<Task<ArrayList<Question>>>()
                            for (lecture in lectureList) {
                                val questionListTask = TaskCompletionSource<ArrayList<Question>>()
                                val lectureId = lecture.id
                                getQuestionListByLectureId(lectureId){
//                                    task.setResult(it)
//                                    lectureTasks.add(questionListTask.task)
                                    questionListTask.setResult(it)
                                }
                                lectureTasks.add(questionListTask.task)
                            }
                            Tasks.whenAllSuccess<ArrayList<Question>>(lectureTasks)
                                .addOnSuccessListener {
                                    task.setResult(it.flatten() as ArrayList<Question>)
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
                mFireStore.collection("replies")
                    .whereEqualTo("questionId", questionId)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            document.reference.delete()
                        }
                    }
                getQuestionListByCourseId(courseId){
                    callback(it)
                }
            }
            .addOnFailureListener {
                callback(ArrayList())
            }
        }

    fun getQuestionsListOfInstructor(instructorId: String, callback: (ArrayList<Question>) -> Unit){
        mFireStore.collection("courses")
            .whereEqualTo("instructor", instructorId)
            .get()
            .addOnSuccessListener { result ->
                val questionTask = result.map { course ->
                    val task = TaskCompletionSource<ArrayList<Question>>()
                    getQuestionListByCourseId(course.id){
                        task.setResult(it)
                    }
                    task.task
                }
                Tasks.whenAllSuccess<ArrayList<Question>>(questionTask)
                    .addOnSuccessListener {
                        callback(it.flatten() as ArrayList<Question>)
                    }
            }
            .addOnFailureListener{exception ->
                callback(ArrayList())
            }
    }

    fun getQuestionNoReplies(questionList: ArrayList<Question>, callback: (ArrayList<Question>) -> Unit) {
        val questionTasks = questionList.map { question ->
            val task = TaskCompletionSource<ArrayList<Question>>()
            mFireStore.collection("replies")
                .whereEqualTo("questionId", question._id)
                .get()
                .addOnSuccessListener { replyList ->
                    if (replyList.isEmpty) {
                        task.setResult(arrayListOf(question))
                    } else {
                        task.setResult(arrayListOf())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("QuestionRepository", exception.toString())
                    task.setException(exception)
                }
            task.task
        }

        Tasks.whenAllSuccess<ArrayList<Question>>(questionTasks)
            .addOnSuccessListener { resultList ->
                val mergedList = resultList.flatten() as ArrayList<Question>
                callback(mergedList)
            }
            .addOnFailureListener { exception ->
                Log.d("QuestionRepository", exception.toString())
            }
    }


    fun getNoInstructorRepliesQuestions(questionList: ArrayList<Question>, instructorId: String, callback: (ArrayList<Question>) -> Unit) {
        val questionTasks = questionList.map { question ->
            val task = TaskCompletionSource<ArrayList<Question>>()
            mFireStore.collection("replies")
                .whereEqualTo("questionId", question._id)
                .get()
                .addOnSuccessListener { replyList ->
                    if (replyList.isEmpty) {
                        task.setResult(arrayListOf(question))
                    } else {
                        val replyTasks = replyList.map { reply ->
                            val replyTask = TaskCompletionSource<Reply>()
                            mFireStore.collection("users")
                                .document(reply.getString("replier")!!)
                                .get()
                                .addOnSuccessListener { user ->
                                    if(user.id != instructorId){
                                        replyTask.setResult(reply.toObject(Reply::class.java))
                                    }
                                    else{
                                        replyTask.setResult(null)
                                    }
                                }
                            replyTask.task
                        }
                        Tasks.whenAllSuccess<Reply>(replyTasks)
                            .addOnSuccessListener { replyResultList ->
                                if (replyResultList.all{it == null}) {
                                    task.setResult(arrayListOf(question))
                                } else {
                                    task.setResult(arrayListOf())
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("QuestionRepository", exception.toString())
                    task.setException(exception)
                }
            task.task
        }

        Tasks.whenAllSuccess<ArrayList<Question>>(questionTasks)
            .addOnSuccessListener { resultList ->
                val mergedList = resultList.flatten() as ArrayList<Question>
                callback(mergedList)
            }
            .addOnFailureListener { exception ->
                Log.d("QuestionRepository", exception.toString())
            }
    }
}