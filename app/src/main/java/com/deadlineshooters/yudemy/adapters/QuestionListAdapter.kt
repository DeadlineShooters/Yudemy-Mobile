package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Question

class QuestionListAdapter (private val questionList: List<Question>): RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {
    var onItemClick: ((Question) -> Unit)? = null

    inner class ViewHolder(listQuestionView: View) : RecyclerView.ViewHolder(listQuestionView) {
        val askerImage: ImageView = listQuestionView.findViewById(R.id.askerImage)
        val questionTitle: TextView = listQuestionView.findViewById(R.id.questionTitle)
        val askerName: TextView = listQuestionView.findViewById(R.id.askerName)
        val askDate: TextView = listQuestionView.findViewById(R.id.askDate)
        val lectureId: TextView = listQuestionView.findViewById(R.id.lectureId)
        val amountReply: TextView = listQuestionView.findViewById(R.id.amountReply)
        val questionContent: TextView = listQuestionView.findViewById(R.id.questionContent)
        val questionImage: ImageView = listQuestionView.findViewById(R.id.questionImage)

        init {
            listQuestionView.setOnClickListener { onItemClick?.invoke(questionList[adapterPosition]) }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(R.layout.question_list_item, parent, false)
        return ViewHolder(courseView)
    }

    override fun onBindViewHolder(holder: QuestionListAdapter.ViewHolder, position: Int) {
        val question: Question = questionList[position]


        val askerImage = holder.askerImage
        val questionTitle = holder.questionTitle
        val askerName = holder.askerName
        val askDate = holder.askDate
        val lectureId = holder.lectureId
        val amountReply = holder.amountReply
        val questionContent = holder.questionContent
        val questionImage = holder.questionImage

        questionTitle.text = question.title
        askerName.text = question.asker
        askDate.text = question.createdTime
        lectureId.text = question.lectureId
        amountReply.text = "No Replies"
        questionContent.text = question.details

        if(question.images.isEmpty()){
            questionImage.visibility = View.GONE
        }
        else{
            questionImage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }
}

