package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.helpers.StringUtils
import com.deadlineshooters.yudemy.models.Lecture

class LecturesAddedAdapter(var lectures: ArrayList<Lecture>): RecyclerView.Adapter<LecturesAddedAdapter.ViewHolder>() {
    private lateinit var context: Context
    var onDeleteLectureClick: ((Int) -> Unit)? = null
    var onLectureTitleChange: ((String, Int) -> Unit)? = null
    var onUploadVideoClick: ((Int) -> Unit)? = null

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val lectureIdx: TextView = listItemView.findViewById(R.id.tvLectureName)
        val lectureTitle: EditText = listItemView.findViewById(R.id.etLectureName)
        val thumnail: ImageView = listItemView.findViewById(R.id.thumbnailLecture)
        val duration: TextView = listItemView.findViewById(R.id.tvVidLen)
        val btnDeleteLecture: TextView = listItemView.findViewById(R.id.btnDeleteLecture)
        val btnUploadVideo: Button = listItemView.findViewById(R.id.btnUploadLecture)
        init {
            btnDeleteLecture.setOnClickListener {
                onDeleteLectureClick?.invoke(bindingAdapterPosition)

            }
            btnUploadVideo.setOnClickListener {
                onUploadVideoClick?.invoke(bindingAdapterPosition)
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

        lecture.index = position + 1

        holder.lectureIdx.text = context.resources.getString(R.string.lecture_idx, position + 1)
        if(lecture.name == "") {
            holder.lectureTitle.text.clear()
            holder.lectureTitle.hint = context.resources.getString(R.string.enter_a_title)
        }
        else
            holder.lectureTitle.setText(lecture.name)

        holder.lectureTitle.doAfterTextChanged {
            onLectureTitleChange?.invoke(it.toString(), holder.bindingAdapterPosition)
        }

        holder.duration.text = StringUtils.secondsToMinuteSecondFormat(lecture.content.duration)

        if(lecture.content.public_id == "") { // local video
            val thumbnailUri = lecture.content.contentUri
            if(thumbnailUri != null) {
            val bitmap = (context as BaseActivity).retriveVideoFrameFromVideo(thumbnailUri)
                Glide.with(context)
                    .load(bitmap)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumnail)
            }
            else {
                Glide.with(context)
                    .load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumnail)
            }
        }
        else { // online video
            val bitmap = (context as BaseActivity).retriveVideoFrameFromVideo(lecture.content.secure_url)
            Glide.with(context)
                .load(bitmap)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumnail)
        }
    }
}