package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Lecture
import kotlin.text.Typography.section

class LecturesAddedAdapter(val lectures: ArrayList<Lecture>): RecyclerView.Adapter<LecturesAddedAdapter.ViewHolder>() {
    private lateinit var context: Context
    var onDeleteLectureClick: ((Int) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val lectureIdx: TextView = listItemView.findViewById(R.id.tvLectureName)
        val lectureTitle: EditText = listItemView.findViewById(R.id.etLectureName)
        val thumnail: ImageView = listItemView.findViewById(R.id.thumbnailLecture)
        val duration: TextView = listItemView.findViewById(R.id.tvVidLen)
        val btnDeleteLecture: TextView = listItemView.findViewById(R.id.btnDeleteLecture)
        init {
            btnDeleteLecture.setOnClickListener {
                onDeleteLectureClick?.invoke(bindingAdapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val lectureView = inflater.inflate(R.layout.lecture_added_item, parent, false)
        return ViewHolder(lectureView)
    }
    override fun getItemCount(): Int {
        return lectures.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lecture = lectures[position]

        holder.lectureIdx.text = context.resources.getString(R.string.lecture_idx, lecture.index)
        if(lecture.name == "")
            holder.lectureTitle.hint = context.resources.getString(R.string.enter_a_title)
        else
            holder.lectureTitle.setText(lecture.name)
        holder.duration.text = context.resources.getString(R.string.video_duration, 2, 45)
//        holder.thumnail.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_video, null))
    }
}