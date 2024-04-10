package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
import com.deadlineshooters.yudemy.repositories.ReplyRepository

class ReplyViewModel : ViewModel() {
    private val replyRepository = ReplyRepository()
    private val _replies = MutableLiveData<ArrayList<Reply>>()
    private val _reply = MutableLiveData<Reply>()

    val reply get() = _reply
    val replies get() = _replies

    fun getRepliesByQuestionId(questionId: String) {
        replyRepository.getReplyListByQuestionId(questionId) {
            _replies.value = it
        }
    }

    fun addNewReply(reply: Reply) {
        replyRepository.addNewReply(reply) {
            _replies.value = it
        }
    }
}