package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.TimeHelper
import com.deadlineshooters.yudemy.models.Lecture

class LectureLearningAdapter(var lectures: List<Map<Lecture, Boolean>>): RecyclerView.Adapter<LectureLearningAdapter.ViewHolder>() {
    private lateinit var context: Context

    var onItemClick: ((Int, Map<Lecture, Boolean>) -> Unit)? = null
    var onLongPress: ((Int, Map<Lecture, Boolean>) -> Unit)? = null
    var selectedLecture: Int = -1

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val index: TextView = listItemView.findViewById(R.id.index)
        val name: TextView = listItemView.findViewById(R.id.lectureName)
        val details: TextView = listItemView.findViewById(R.id.details)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(bindingAdapterPosition, lectures[bindingAdapterPosition])
            }

            itemView.setOnLongClickListener {
                onLongPress?.invoke(bindingAdapterPosition, lectures[bindingAdapterPosition])
                true
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
        val lecture = lectures[position].keys.first()
        val isFinished = lectures[position].values.first()
        // TODO: get lecture details
        holder.index.text = lecture.index.toString()

        if(!isFinished)
            holder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        else
            holder.name.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_checkbox_circle, null), null, null, null)

        holder.name.text = lecture.name

        holder.details.text = context.resources.getString(R.string.learning_lecture_detail, lecture.content.resource_type, TimeHelper().convertDurationToString(lecture.content.duration))

        if(selectedLecture == position) {
            holder.itemView.background = ResourcesCompat.getDrawable(context.resources, R.color.purple_alpha, null)
        } else {
            holder.itemView.background = null
        }
    }
}