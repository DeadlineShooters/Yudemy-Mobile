package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.UserLecture

class LectureLearningAdapter(private val lectures: List<UserLecture>): RecyclerView.Adapter<LectureLearningAdapter.ViewHolder>() {
    private lateinit var context: Context
    var onItemClick: ((UserLecture) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val index: TextView = listItemView.findViewById(R.id.index)
        val name: TextView = listItemView.findViewById(R.id.lectureName)
        val details: TextView = listItemView.findViewById(R.id.details)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(lectures[bindingAdapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val lectureView = inflater.inflate(R.layout.lecture_learning_item, parent, false)
        return ViewHolder(lectureView)
    }
    override fun getItemCount(): Int {
        return lectures.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lecture = lectures[position]
        // TODO: get lecture details
        holder.index.text = "1"
        holder.name.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_checkbox_circle, null), null, null, null)
        holder.name.text = "Introduction"
        holder.details.text = context.resources.getString(R.string.learning_lecture_detail, 13, 45)
    }
}