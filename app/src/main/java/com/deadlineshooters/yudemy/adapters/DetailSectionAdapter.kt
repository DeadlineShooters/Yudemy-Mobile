package com.deadlineshooters.yudemy.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures

class DetailSectionAdapter(private val sectionsWithLecturesList: List<SectionWithLectures>) : RecyclerView.Adapter<DetailSectionAdapter.SectionViewHolder>() {
    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sectionTitle: TextView = itemView.findViewById(R.id.tv_section)
        val lectureRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_lectures)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.section_item, parent, false)
        return SectionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        Log.d(this.javaClass.simpleName, sectionsWithLecturesList[position].section.title)
        val currentItem = sectionsWithLecturesList[position]
        holder.sectionTitle.text = holder.itemView.context.getString(R.string.section_title, currentItem.section.index, currentItem.section.title)

        // Initialize the RecyclerView with its adapter
        val lectureAdapter = DetailLectureAdapter(currentItem.lectures)
        holder.lectureRecyclerView.adapter = lectureAdapter


        holder.lectureRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.lectureRecyclerView.adapter = lectureAdapter

    }

    override fun getItemCount() = sectionsWithLecturesList.size
}

