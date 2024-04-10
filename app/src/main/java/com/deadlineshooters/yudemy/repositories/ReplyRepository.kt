package com.deadlineshooters.yudemy.repositories

import com.deadlineshooters.yudemy.models.Reply
import com.google.firebase.firestore.FirebaseFirestore

class ReplyRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val repliesCollection = mFireStore.collection("replies")

    fun getReplyListByQuestionId(questionId: String, callback: (ArrayList<Reply>) -> Unit){
        repliesCollection.whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { result ->
                val list: ArrayList<Reply> = ArrayList()
                for (document in result) {
                    val reply = document.toObject(Reply::class.java)
                    list.add(reply)
                }
                callback(list)
            }
            .addOnFailureListener { exception ->
                callback(ArrayList())
            }
    }
}