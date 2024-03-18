package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.Video
import java.util.ArrayList

class SectionsAddedAdapter(private val sections: List<Section>): RecyclerView.Adapter<SectionsAddedAdapter.ViewHolder>() {
    lateinit var context: Context
    var onAddLectureClick: ((LecturesAddedAdapter, Int) -> Unit)? = null
    var onDeleteSectionClick: ((Int) -> Unit)? = null
    var onDeleteLectureClick: ((LecturesAddedAdapter, Int, Int) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val sectionIdx: TextView = listItemView.findViewById(R.id.tvSectionIdx)
        val sectionName: EditText = listItemView.findViewById(R.id.etSectionName)
        val lectureList: RecyclerView = listItemView.findViewById(R.id.addedLectures)
        val btnAddLecture: TextView = listItemView.findViewById(R.id.btnAddLecture)
        val lecturesAddedAdapter = LecturesAddedAdapter(ArrayList<Lecture>())
        val btnDeleteSection: TextView = listItemView.findViewById(R.id.btnDeleteSection)
        init {
            lecturesAddedAdapter.onDeleteLectureClick = {
                onDeleteLectureClick?.invoke(lecturesAddedAdapter, it, bindingAdapterPosition)
                lectureList.smoothScrollToPosition(it)
            }

            btnAddLecture.setOnClickListener {
                onAddLectureClick?.invoke(lecturesAddedAdapter, bindingAdapterPosition)
                lectureList.smoothScrollToPosition(lecturesAddedAdapter.itemCount)
            }

            btnDeleteSection.setOnClickListener {
                onDeleteSectionClick?.invoke(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val sectionView = inflater.inflate(R.layout.section_added_item, parent, false)
        return ViewHolder(sectionView)
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section: Section = sections[position]

        holder.sectionIdx.text = context.resources.getString(R.string.section_idx, section.index)
        if(section.title == "")
            holder.sectionName.hint = context.resources.getString(R.string.enter_a_title)
        else
            holder.sectionName.setText(section.title)

        holder.lectureList.adapter = holder.lecturesAddedAdapter
        holder.lectureList.layoutManager = LinearLayoutManager(context)
    }
}