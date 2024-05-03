package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.net.Uri
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
import java.io.File
import java.util.Collections

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

        if(lecture.content.contentUri != null) { // local video
            Glide
                .with(context)
                .asBitmap()
                .load(Uri.fromFile(File(lecture.content.secure_url)))
                .into((holder.thumnail))
            holder.duration.text = StringUtils.secondsToMinuteSecondFormat((context as BaseActivity).getVideoDuration(lecture.content.contentUri!!))
        }
        else { // online video
            Glide.with(context)
                .load(lecture.content.secure_url)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumnail)
2
            holder.duration.text = StringUtils.secondsToMinuteSecondFormat(lecture.content.duration)
        }
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(lectures, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(lectures, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        if(fromPosition < toPosition)
            notifyItemRangeChanged(fromPosition, toPosition - fromPosition + 1)
        else
            notifyItemRangeChanged(toPosition, fromPosition - toPosition + 1)
    }
}