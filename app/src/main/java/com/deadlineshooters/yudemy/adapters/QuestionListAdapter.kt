package com.deadlineshooters.yudemy.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.viewmodels.LectureViewModel
import com.deadlineshooters.yudemy.viewmodels.QuestionViewModel
import com.deadlineshooters.yudemy.viewmodels.ReplyViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

class QuestionListAdapter (var questionList: ArrayList<Question>, private val lifecycleOwner: LifecycleOwner, val questionViewModel: QuestionViewModel): RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {
    var onItemClick: ((Question) -> Unit)? = null
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())
    private lateinit var userViewModel: UserViewModel
    private lateinit var lectureViewModel: LectureViewModel
    private lateinit var replyViewModel: ReplyViewModel

    inner class ViewHolder(listQuestionView: View) : RecyclerView.ViewHolder(listQuestionView) {
        val askerImage: ImageView = listQuestionView.findViewById(R.id.askerImage)
        val questionTitle: TextView = listQuestionView.findViewById(R.id.questionTitle)
        val askerName: TextView = listQuestionView.findViewById(R.id.askerName)
        val askDate: TextView = listQuestionView.findViewById(R.id.askDate)
        val lectureId: TextView = listQuestionView.findViewById(R.id.lectureId)
        val amountReply: TextView = listQuestionView.findViewById(R.id.amountReply)
        val questionContentView: ConstraintLayout = listQuestionView.findViewById(R.id.questionContentView)
        val questionContent: TextView = listQuestionView.findViewById(R.id.questionContent)
        val questionImageContainer: LinearLayout = listQuestionView.findViewById(R.id.questionImageContainer)

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
        val questionTitle = holder.questionTitle
        val askerName = holder.askerName
        val askDate = holder.askDate
        val lectureId = holder.lectureId
        val amountReply = holder.amountReply
        val questionContent = holder.questionContent
        val questionContentView = holder.questionContentView
        val questionImageContainer = holder.questionImageContainer
        val askerImage = holder.askerImage

        userViewModel = UserViewModel()
        lectureViewModel = LectureViewModel()
        replyViewModel = ReplyViewModel()

        questionTitle.text = question.title

        userViewModel.getUserById(question.asker)
        lectureViewModel.getLectureById(question.lectureId)
        replyViewModel.getRepliesByQuestionId(question._id)

        userViewModel.userData.observe(lifecycleOwner, Observer { it ->
            if (it.id == question.asker) {
                askerName.text = it.fullName
                ImageViewHelper().setImageViewFromUrl(it.image, askerImage)
            }
        })


        val date: Date = originalFormat.parse(question.createdTime) ?: Date()
        val formattedDate: String = newFormat.format(date)
        askDate.text = formattedDate

        lectureId.text = question.lectureId
        lectureViewModel.lecture.observe(lifecycleOwner, Observer { it ->
            lectureId.text = it.name
        })

        replyViewModel.replies.observe(lifecycleOwner, Observer { it ->
            if(it.size == 0) amountReply.text = "No Replies"
            else{
                amountReply.text = "${it.size} Replies"
            }
        })

        questionContent.text = question.details


        if(question.images.isNotEmpty()){
            questionImageContainer.removeAllViews()
            for (imageUrl in question.images) {
                val imageView = ImageView(questionContentView.context)
                imageView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    350
                )
                imageView.adjustViewBounds = true
                imageView.setPadding(0, 0, 16, 16)
                ImageViewHelper().setImageViewFromUrl(imageUrl, imageView)
                questionImageContainer.addView(imageView)
            }
        }
    }


    override fun getItemCount(): Int {
        return questionList.size
    }

    fun filterQuestion(filter: ArrayList<String>, currentLectureId: String) {

        questionList = questionViewModel.questions.value!!
        notifyDataSetChanged()

        Log.d("cho na", questionList.toString())
        questionList.sortByDescending { it.createdTime }


        var tmpQuestionList: ArrayList<Question> = arrayListOf()
        var resQuestionList: ArrayList<Question> = arrayListOf()


        if(filter[0] == "All lectures") {
            tmpQuestionList = questionList
        }
        else if(filter[0] == "Current lecture") {
            questionList.forEach { question ->
                if (question.lectureId == currentLectureId) {
                    tmpQuestionList.add(question)
                }
            }
        }


        if(filter[2] == "Question without responses") {
            questionViewModel.getQuestionNoReplies(tmpQuestionList)
            questionViewModel.questionNoReplies.observe(lifecycleOwner, Observer { it ->
                questionList = it
                notifyDataSetChanged()
            })
        } else if (filter[2] == "Question I asked") {
            questionList.forEach { question ->
                if (question.asker == BaseActivity().getCurrentUserID()) {
                    resQuestionList.add(question)
                }
                questionList = resQuestionList
                notifyDataSetChanged()

            }
        } else if (filter[2] == "All questions"){
            questionList = tmpQuestionList
            notifyDataSetChanged()
        }
    }
    fun instructorFilterQuestion(filter: String) {
        questionList = questionViewModel.questions.value!!
        notifyDataSetChanged()

        var tmpQuestionList: ArrayList<Question> = questionList

        if(filter == "All questions"){
            questionList = tmpQuestionList
            notifyDataSetChanged()
        } else if(filter == "Questions without responses"){
            questionViewModel.getQuestionNoReplies(tmpQuestionList)
            questionViewModel.questionNoReplies.observe(lifecycleOwner, Observer { it ->
                questionList = it
                notifyDataSetChanged()
            })
        } else if (filter == "Questions without instructors' responses"){
            questionViewModel.getNoInstructorRepliesQuestions(tmpQuestionList)
            questionViewModel.questionNoReplies.observe(lifecycleOwner, Observer { it ->
                questionList = it
                notifyDataSetChanged()
            })
        }
    }
}

