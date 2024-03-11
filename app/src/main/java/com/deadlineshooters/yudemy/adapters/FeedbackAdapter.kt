package com.deadlineshooters.yudemy.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.fragments.FeedbackFragment
import com.deadlineshooters.yudemy.models.CourseFeedback

class FeedbackAdapter(private val context: FeedbackFragment, private val feedbackList: List<CourseFeedback>) : RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {
    var listener: AdapterView.OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

        val ivCourseImage: ImageView = itemView.findViewById(R.id.iv_courseImage)
        val fullname: TextView = itemView.findViewById(R.id.fullname)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarItem)
        val tvReview: TextView = itemView.findViewById(R.id.tv_review)
        val btnRespond: Button = itemView.findViewById(R.id.btn_respond)
        // val btnRespond: MaterialButton = itemView.findViewById(R.id.btn_respond)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(null, v, position, itemId)
            }
        }
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedback = feedbackList[position]
        // TODO: Fetch data
//        holder.tvTitle.text = feedback.courseTitle
//        holder.ivCourseImage.setImageResource(feedback.courseImageResId)
//        holder.fullname.text = feedback.authorName
//        holder.timestamp.text = feedback.lastUpdated
//        holder.ratingBar.rating = feedback.rating.toFloat()
//        holder.tvReview.text = feedback.review

        // TODO 2: Delete dummy data
        holder.tvTitle.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor"
        holder.ivCourseImage.setImageResource(R.drawable.ic_launcher_background)
        holder.fullname.text = "John Doe"
        holder.timestamp.text = "2 days ago"
        holder.ratingBar.rating = 4.0F
        holder.tvReview.text = "Great course"
        // holder.binding.btnRespond.setOnClickListener { /* Handle respond button click here */ }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(context.context)
        val view = inflater.inflate(R.layout.feedback_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return feedbackList.size

    }

}
