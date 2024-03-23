package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.UserLecture
import kotlin.collections.ArrayList

class CourseLearningAdapter(private val sections: List<Section>, private val lectures: ArrayList<ArrayList<Map<Lecture, Boolean>>>): RecyclerView.Adapter<CourseLearningAdapter.ViewHolder>() {
    lateinit var context: Context
    var onItemClick: ((Lecture) -> Unit)? = null

    private var selectedSection: Int = 0
    private var selectedLecture: Int = 0

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val sectionTitle: TextView = listItemView.findViewById(R.id.sectionTitle)
        val lectureList: RecyclerView = listItemView.findViewById(R.id.lectureList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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

        val lectureLearningAdapter = LectureLearningAdapter(lectures[position])
        lectureLearningAdapter.onItemClick = { lecturePosition, lecture ->
            selectedSection = position
            selectedLecture = lecturePosition
            notifyDataSetChanged()

            onItemClick?.invoke(lecture)
        }
        if(selectedSection != position) {
            lectureLearningAdapter.selectedLecture = -1
            lectureLearningAdapter.notifyDataSetChanged()
        }
        else {
            lectureLearningAdapter.selectedLecture = selectedLecture
            lectureLearningAdapter.notifyDataSetChanged()
        }

        holder.lectureList.adapter = lectureLearningAdapter
        holder.lectureList.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    fun createDummyData(): ArrayList<UserLecture> {
        val lectures = ArrayList<UserLecture>()
        for(i in 1..5) {
            val l = UserLecture("1", "Introduction", "This is the introduction", false)
            l._id = i.toString()
            lectures.add(l)
        }
        return lectures
    }
}