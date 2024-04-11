package com.deadlineshooters.yudemy.adapters

import android.annotation.SuppressLint
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
import kotlin.collections.ArrayList

class CourseLearningAdapter(private val sections: List<Section>, private val lectures: ArrayList<ArrayList<Map<Lecture, Boolean>>>): RecyclerView.Adapter<CourseLearningAdapter.ViewHolder>() {
    lateinit var context: Context

    var onItemClick: ((Map<Lecture, Boolean>, Int, Int) -> Unit)? = null
    var onLongPress: ((Map<Lecture, Boolean>, Int, Int) -> Unit)? = null

    private var selectedSection: Int = 0
    private var selectedLecture: Int = 0

    private val lectureAdapters = mutableListOf<LectureLearningAdapter>()
    private var currentAdapter: LectureLearningAdapter? = null

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
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val section: Section = sections[position]

        holder.sectionTitle.text = context.resources.getString(R.string.learning_section_title, section.index, section.title)

        val lectureLearningAdapter = LectureLearningAdapter(lectures[position])
        lectureAdapters.add(lectureLearningAdapter)

        if(currentAdapter == null) {
            currentAdapter = lectureLearningAdapter
        }

        lectureLearningAdapter.onItemClick = { lecturePosition, lecture ->
            selectedSection = position
            selectedLecture = lecturePosition

            currentAdapter?.selectedLecture = -1
            lectureLearningAdapter.selectedLecture = lecturePosition

            lectureLearningAdapter.notifyDataSetChanged()
            currentAdapter?.notifyDataSetChanged()

            currentAdapter = lectureLearningAdapter

            onItemClick?.invoke(lecture, selectedSection, selectedLecture)
        }

        lectureLearningAdapter.onLongPress = { lecturePosition, userLecture ->
            onLongPress?.invoke(userLecture, position, lecturePosition)
        }

        if(selectedSection == position) {
            lectureLearningAdapter.selectedLecture = selectedLecture
            lectureLearningAdapter.notifyItemChanged(selectedLecture)
        }

        holder.lectureList.adapter = lectureLearningAdapter
        holder.lectureList.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    fun notifyLectureMarked(sectionIdx: Int, lectureIdx: Int) {
        lectureAdapters[sectionIdx].notifyItemChanged(lectureIdx)
    }
}