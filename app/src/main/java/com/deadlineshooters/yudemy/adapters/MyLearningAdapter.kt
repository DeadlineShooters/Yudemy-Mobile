package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Course

class MyLearningAdapter(private val courses: ArrayList<Map<Course, String>>, private val progresses: ArrayList<Int>): RecyclerView.Adapter<MyLearningAdapter.ViewHolder>() {
    var context: Context? = null
    var onItemClick: ((Course, String) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val thumbnailView: ImageView = listItemView.findViewById(R.id.thumbnail)
        val name: TextView = listItemView.findViewById(R.id.name)
        val lecturer: TextView = listItemView.findViewById(R.id.lecturer)
        val progressBar: ProgressBar = listItemView.findViewById(R.id.courseProgressBar)
        val progressText: TextView = listItemView.findViewById(R.id.progress)

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(courses[bindingAdapterPosition].keys.first(), courses[bindingAdapterPosition].values.first())
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(R.layout.my_learning_list_item, parent, false)
        return ViewHolder(courseView)
    }
    override fun getItemCount(): Int {
        return courses.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course: Course = courses[position].keys.first()
        ImageViewHelper().setImageViewFromUrl(course.thumbnail, holder.thumbnailView)
        holder.name.text = course.name
        holder.lecturer.text = courses[position].values.first()
        val progress = progresses[position]
        when(progress) {
            0 -> {
                holder.progressBar.visibility = View.GONE
                holder.progressText.text = context?.resources?.getString(R.string.start_course)
                holder.progressText.setTypeface(null, Typeface.BOLD)
                holder.progressText.setTextColor(context?.resources?.getColor(R.color.primary_color, null)!!)
            }
            100 -> {
                holder.progressBar.progress = progress
                holder.progressText.text = context?.resources?.getString(R.string.completed)
            }
            else -> {
                holder.progressBar.progress = progress
                holder.progressText.text = context?.resources?.getString(R.string.progress_complete, progress)
            }
        }
    }
}