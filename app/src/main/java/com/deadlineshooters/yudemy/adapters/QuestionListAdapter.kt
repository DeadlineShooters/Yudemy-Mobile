package com.deadlineshooters.yudemy.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.ui.layout.LookaheadLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.repositories.QuestionRepository
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.deadlineshooters.yudemy.viewmodels.LectureViewModel
import com.deadlineshooters.yudemy.viewmodels.QuestionViewModel
import com.deadlineshooters.yudemy.viewmodels.ReplyViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionListAdapter (var questionList: ArrayList<Question>, private val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {
    var onItemClick: ((Question) -> Unit)? = null
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())
    private lateinit var userViewModel: UserViewModel
    private lateinit var lectureViewModel: LectureViewModel
    private lateinit var replyViewModel: ReplyViewModel
    private lateinit var questionViewModel: QuestionViewModel

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
        userViewModel = UserViewModel()
        lectureViewModel = LectureViewModel()
        replyViewModel = ReplyViewModel()
        questionViewModel = QuestionViewModel()
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


    override fun getItemCount(): Int {
        return questionList.size
    }

    fun filterQuestion(filter: ArrayList<String>, currentLectureId: String) {
        Log.d("QuestionListAdapter", filter.toString())
        val tmpQuestionList: ArrayList<Question> = arrayListOf()
        val resQuestionList: ArrayList<Question> = arrayListOf()
        if(filter[0] == "All lectures") {
            questionList.forEach { question ->
                tmpQuestionList.add(question)
            }
            notifyDataSetChanged()
        }
        else if(filter[0] == "Current lecture") {
            questionList.forEach { question ->
                if (question.lectureId == currentLectureId) {
                    tmpQuestionList.add(question)
                }
            }
        }

        Log.d("QuestionListAdapter", tmpQuestionList.toString())

        tmpQuestionList.sortByDescending { it.createdTime }


        questionViewModel.getQuestionNoReplies(tmpQuestionList)

        if(filter[2] == "Question without responses") {
            questionViewModel.questionNoReplies.observe(lifecycleOwner, Observer { it ->
                Log.d("QuestionListAdapter", it.size.toString())
                resQuestionList.addAll(it)
            })
        } else if (filter[2] == "Question I asked") {
            questionList.forEach { question ->
                if (question.asker == BaseActivity().getCurrentUserID()) {
                    resQuestionList.add(question)
                }
            }
//            questionList.clear()
//            questionList.addAll(tmpQuestionList)
//            notifyDataSetChanged()
        } else{
            resQuestionList.addAll(tmpQuestionList)
        }

        Log.d("QuestionListAdapter", resQuestionList.toString())
        Log.d("QuestionListAdapter", resQuestionList.size.toString())
        questionList = resQuestionList
        notifyDataSetChanged()
    }

}

