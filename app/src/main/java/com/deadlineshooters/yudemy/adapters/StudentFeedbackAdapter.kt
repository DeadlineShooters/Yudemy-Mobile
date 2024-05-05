package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository

class StudentFeedbackAdapter(private val feedbackList: ArrayList<CourseFeedback>) : RecyclerView.Adapter<StudentFeedbackAdapter.FeedbackViewHolder>() {
    private val courseFeedbackRepo = CourseFeedbackRepository()

    class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val rbReview: RatingBar = itemView.findViewById(R.id.rb_review)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvFeedback: TextView = itemView.findViewById(R.id.tv_feedback)
        val llInstructorResponse: LinearLayout = itemView.findViewById(R.id.ll_instructorResponse)
        val tvInstructorName: TextView = itemView.findViewById(R.id.tv_instructorName)
        val tvResponseDate: TextView = itemView.findViewById(R.id.tv_responseDate)
        val tvResponse: TextView = itemView.findViewById(R.id.tv_response)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.detail_feedback_layout, parent, false)
        return FeedbackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val currentItem = feedbackList[position]
        courseFeedbackRepo.getUserFullName(currentItem.userId) { fullName ->
            holder.tvName.text  = fullName
        }
        holder.tvName.text
        holder.rbReview.rating = currentItem.rating.toFloat()
        holder.tvDate.text = currentItem.createdDatetime
        holder.tvFeedback.text = currentItem.feedback

        // If there is a response from the instructor, show it
        if (currentItem.instructorResponse != null) {
            holder.llInstructorResponse.visibility = View.VISIBLE
            courseFeedbackRepo.getUserFullName(currentItem.instructorResponse!!.instructorId) { fullName ->
                holder.tvInstructorName.text  = fullName
            }
            holder.tvResponseDate.text = currentItem.instructorResponse!!.createdDatetime
            holder.tvResponse.text = currentItem.instructorResponse!!.content
        } else {
            holder.llInstructorResponse.visibility = View.GONE
        }
    }

    override fun getItemCount() = feedbackList.size
}
