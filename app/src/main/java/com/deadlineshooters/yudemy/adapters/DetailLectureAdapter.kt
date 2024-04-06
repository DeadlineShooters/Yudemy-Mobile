package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.StringUtils
import com.deadlineshooters.yudemy.models.Lecture

class DetailLectureAdapter(private val lectureList: List<Lecture>) : RecyclerView.Adapter<DetailLectureAdapter.LectureViewHolder>() {

    class LectureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lectureNumber: TextView = itemView.findViewById(R.id.lecture_number)
        val lectureTitle: TextView = itemView.findViewById(R.id.lecture_title)
        val lectureDuration: TextView = itemView.findViewById(R.id.lecture_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lecture_item, parent, false)
        return LectureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val currentItem = lectureList[position]
        holder.lectureNumber.text = currentItem.index.toString()
        holder.lectureTitle.text = currentItem.name
        holder.lectureDuration.text = StringUtils.secondsToMinuteSecondFormat(currentItem.content.duration)
    }

    override fun getItemCount() = lectureList.size
}

