package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.QuestionListAdapter
import com.deadlineshooters.yudemy.models.Question

class QAActivity : AppCompatActivity() {
    private lateinit var qaCloseBtn: TextView
    private lateinit var qaFilterBtn: Button
    private lateinit var addQuestionBtn: Button
    private lateinit var questionListView: RecyclerView

    private val dumpQuestion1 = Question("123", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion2 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion3 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestionList = arrayListOf(dumpQuestion1, dumpQuestion2, dumpQuestion3)

    private var questionListAdapter = QuestionListAdapter(dumpQuestionList)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_q_a)

        qaCloseBtn = findViewById(R.id.qaCloseBtn)
        qaFilterBtn = findViewById(R.id.qaFilterBtn)
        addQuestionBtn = findViewById(R.id.addQuestionBtn)
        questionListView = findViewById(R.id.questionListView)

        questionListAdapter = QuestionListAdapter(dumpQuestionList)
        questionListView.adapter = questionListAdapter
        questionListView.layoutManager = LinearLayoutManager(this)

        qaCloseBtn.setOnClickListener {
            //TODO: Close the activity
        }


    }


}