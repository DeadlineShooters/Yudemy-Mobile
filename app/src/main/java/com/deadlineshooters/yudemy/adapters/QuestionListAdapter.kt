package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Question
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionListAdapter (val questionList: List<Question>): RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {
    var onItemClick: ((Question) -> Unit)? = null
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())

    inner class ViewHolder(listQuestionView: View) : RecyclerView.ViewHolder(listQuestionView) {
        val askerImage: ImageView = listQuestionView.findViewById(R.id.askerImage)
        val questionTitle: TextView = listQuestionView.findViewById(R.id.questionTitle)
        val askerName: TextView = listQuestionView.findViewById(R.id.askerName)
        val askDate: TextView = listQuestionView.findViewById(R.id.askDate)
        val lectureId: TextView = listQuestionView.findViewById(R.id.lectureId)
        val amountReply: TextView = listQuestionView.findViewById(R.id.amountReply)
        val questionContentView: ConstraintLayout = listQuestionView.findViewById(R.id.questionContentView)
        val questionContent: TextView = listQuestionView.findViewById(R.id.questionContent)
        val questionImage: ImageView = listQuestionView.findViewById(R.id.questionImage)

        init {
            listQuestionView.setOnClickListener {
                onItemClick?.invoke(questionList[bindingAdapterPosition])
            }
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

        val date: Date = originalFormat.parse(question.createdTime) ?: Date()
        val formattedDate: String = newFormat.format(date)
        askDate.text = formattedDate

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

