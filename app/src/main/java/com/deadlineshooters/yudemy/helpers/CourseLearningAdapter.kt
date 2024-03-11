package com.deadlineshooters.yudemy.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.UserLecture
import com.google.firebase.Timestamp
import java.util.ArrayList

class CourseLearningAdapter(private val sections: List<Section>, private val userId: String): RecyclerView.Adapter<CourseLearningAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var lectureLearningAdapter: LectureLearningAdapter
    var onItemClick: ((UserLecture) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val sectionTitle: TextView = listItemView.findViewById(R.id.sectionTitle)
        val lectureList: RecyclerView = listItemView.findViewById(R.id.lectureList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseLearningAdapter.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val sectionView = inflater.inflate(R.layout.section_learning_item, parent, false)
        return ViewHolder(sectionView)
    }
    override fun getItemCount(): Int {
        return sections.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section: Section = sections[position]

        holder.sectionTitle.text = context.resources.getString(R.string.learning_section_title, section.index, section.title)
        // TODO: get lecture list of this user
        lectureLearningAdapter = LectureLearningAdapter(createDummyData())
        holder.lectureList.adapter = lectureLearningAdapter
        holder.lectureList.layoutManager = LinearLayoutManager(holder.itemView.context)

        lectureLearningAdapter.onItemClick = {
            onItemClick?.invoke(it)
        }
    }

    fun createDummyData(): ArrayList<UserLecture> {
        val lectures = ArrayList<UserLecture>()
        for(i in 1..5) {
            val l = UserLecture("1", "Introduction", "This is the introduction", 0, Timestamp.now())
            l._id = i.toString()
            lectures.add(l)
        }
        return lectures
    }
}