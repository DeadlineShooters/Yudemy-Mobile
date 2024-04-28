package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.fragments.CourseDraftingMenuFragment
import com.deadlineshooters.yudemy.fragments.FeedbackFragment
import com.deadlineshooters.yudemy.helpers.DialogHelper
import com.deadlineshooters.yudemy.helpers.FragmentHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository
import com.deadlineshooters.yudemy.repositories.UserRepository

class FeedbackAdapter(
    private val context: FeedbackFragment,
    private var feedbackList: List<CourseFeedback>
) : RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {
    var listener: AdapterView.OnItemClickListener? = null
    private val courseFeedbackRepo = CourseFeedbackRepository()
    private val userRepo = UserRepository()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

        val ivCourseImage: ImageView = itemView.findViewById(R.id.iv_courseImage)
        val fullname: TextView = itemView.findViewById(R.id.fullname)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarItem)
        val tvReview: TextView = itemView.findViewById(R.id.tv_review)
        val ivAvatar: ImageView = itemView.findViewById(R.id.avatar)
        val btnRespond: Button = itemView.findViewById(R.id.btn_respond)
        val llCourseTitle : LinearLayout = itemView.findViewById(R.id.ll_courseTitle)

        // Views for instructor response management
        val llInstructorResponseManagement: LinearLayout = itemView.findViewById(R.id.ll_instructorResponse_management)
        val ivYourAvatar: ImageView = itemView.findViewById(R.id.iv_yourAvatar)
        val tvYourTimestamp: TextView = itemView.findViewById(R.id.tv_yourTimestamp)
        val tvYourResponse: TextView = itemView.findViewById(R.id.tv_yourResponse)
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

    fun updateCourseFeedback(newCourseFeedbackList: List<CourseFeedback>) {
        feedbackList = newCourseFeedbackList

        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedback = feedbackList[position]

        holder.tvTitle.text = feedback.course!!.name
        Glide.with(holder.itemView.context)
            .load(feedback.course?.thumbnail?.secure_url)
            .placeholder(R.drawable.placeholder)
            .into(holder.ivCourseImage)

        userRepo.getUserById(feedback.userId) { user ->
            Glide.with(holder.itemView.context)
                .load(user!!.image.public_id)
                .placeholder(R.drawable.placeholder_avatar)
                .into(holder.ivAvatar)
            holder.fullname.text = user.fullName
        }


        holder.timestamp.text = feedback.createdDatetime
        holder.ratingBar.rating = feedback.rating.toFloat()
        holder.tvReview.text = feedback.feedback

        holder.llCourseTitle.setOnClickListener {
            FragmentHelper.replaceFragment(CourseDraftingMenuFragment(), feedback.course, context)

        }

        if (feedback.instructorResponse != null) {
            holder.btnRespond.text = "View response"

            holder.btnRespond.setOnClickListener {
                holder.btnRespond.visibility = View.GONE

                // show response
                userRepo.getUserById(UserRepository.getCurrentUserID()) { user ->
                    Glide.with(holder.itemView.context)
                        .load(user!!.image.public_id)
                        .placeholder(R.drawable.placeholder_avatar)
                        .into(holder.ivYourAvatar)

                }

                holder.tvYourTimestamp.text = feedback.instructorResponse!!.createdDatetime
                holder.tvYourResponse.text = feedback.instructorResponse!!.content
                holder.llInstructorResponseManagement.visibility = View.VISIBLE

            }

        } else {
            holder.btnRespond.text = "Respond"
            holder.btnRespond.setOnClickListener {
                // Create an EditText field
                val input = EditText(holder.itemView.context)

                // Create an AlertDialog
                val dialog = AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Enter your response")
                    .setView(input)
                    .setPositiveButton("OK") { dialog, _ ->
                        val text = input.text.toString()

                        DialogHelper.showProgressDialog(context.requireContext(), "Sending...")
                        // TODO 2: update data
                        courseFeedbackRepo.saveFeedbackResponse(feedback.course!!.instructor, feedback.id, text) { result, updatedCourseFeedback ->
                            DialogHelper.hideProgressDialog()

                            if (result) {
                                // show the response on UI
                                holder.btnRespond.visibility = View.GONE

                                // show response
                                userRepo.getUserById(UserRepository.getCurrentUserID()) { user ->
                                    Glide.with(holder.itemView.context)
                                        .load(user!!.image.public_id)
                                        .placeholder(R.drawable.placeholder_avatar)
                                        .into(holder.ivYourAvatar)
                                }

                                holder.tvYourTimestamp.text = updatedCourseFeedback!!.instructorResponse!!.createdDatetime
                                holder.tvYourResponse.text = updatedCourseFeedback.instructorResponse!!.content
                                holder.llInstructorResponseManagement.visibility = View.VISIBLE
                            } else {
                                // Handle the error case
                            }
                        }

                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()

                // Show the AlertDialog
                dialog.show()

                // Show the keyboard
                DialogHelper.showKeyboard(input, context.requireContext())


            }

        }


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
