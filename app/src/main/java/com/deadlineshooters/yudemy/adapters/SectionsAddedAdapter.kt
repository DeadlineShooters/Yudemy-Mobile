package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.CustomItemTouchHelper
import com.deadlineshooters.yudemy.interfaces.ItemTouchListener
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.SectionWithLectures
import java.util.Collections


class SectionsAddedAdapter(private val sections: ArrayList<SectionWithLectures>): RecyclerView.Adapter<SectionsAddedAdapter.ViewHolder>() {
    lateinit var context: Context
    var onAddLectureClick: ((Int) -> Unit)? = null
    var onDeleteSectionClick: ((Int) -> Unit)? = null
    var onDeleteLectureClick: ((Int, Int) -> Unit)? = null

    var onSectionTitleChange: ((String, Int) -> Unit)? = null
    var onLectureTitleChange: ((String, Int, Int) -> Unit)? = null

    var onUploadVideoClick: ((Int, Int) -> Unit)? = null

    var lectureAdapters = ArrayList<LecturesAddedAdapter>()

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val sectionIdx: TextView = listItemView.findViewById(R.id.tvSectionIdx)
        val sectionName: EditText = listItemView.findViewById(R.id.etSectionName)
        val lectureList: RecyclerView = listItemView.findViewById(R.id.addedLectures)
        val btnAddLecture: TextView = listItemView.findViewById(R.id.btnAddLecture)
        val btnDeleteSection: TextView = listItemView.findViewById(R.id.btnDeleteSection)
        init {
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
        val section: Section = sections[position].section
        Log.d("SectionsAddedAdapter", "onBindViewHolder: section $section")

        section.index = position + 1

        holder.sectionIdx.text = context.resources.getString(R.string.section_idx, position + 1)
        if(section.title == "") {
            holder.sectionName.text.clear()
            holder.sectionName.hint = context.resources.getString(R.string.enter_a_title)
        }
        else
            holder.sectionName.setText(section.title)

        holder.sectionName.doAfterTextChanged {
            onSectionTitleChange?.invoke(it.toString(), holder.bindingAdapterPosition)
        }

        val lecturesAddedAdapter: LecturesAddedAdapter
        if (lectureAdapters.size > position) {
            // If an adapter already exists for this position, use it
            lecturesAddedAdapter = lectureAdapters[position]
        } else {
            // If no adapter exists for this position, create a new one
            lecturesAddedAdapter = LecturesAddedAdapter(sections[position].lectures as ArrayList<Lecture>)
            lectureAdapters.add(lecturesAddedAdapter)
        }
        Log.d("SectionsAddedAdapter", "onBindViewHolder: $lectureAdapters")

        lecturesAddedAdapter.onDeleteLectureClick = {
            onDeleteLectureClick?.invoke(it, holder.bindingAdapterPosition)
            if(lecturesAddedAdapter.itemCount != 0) {
                if(it == lecturesAddedAdapter.itemCount) {
                    holder.lectureList.smoothScrollToPosition(it - 1)
                }
                else {
                    holder.lectureList.smoothScrollToPosition(it)
                }
            }
        }
        lecturesAddedAdapter.onLectureTitleChange = { title, lecturePosition ->
            onLectureTitleChange?.invoke(title, holder.bindingAdapterPosition, lecturePosition)
        }
        lecturesAddedAdapter.onUploadVideoClick = {
            onUploadVideoClick?.invoke(holder.bindingAdapterPosition, it)
        }

        val callback: ItemTouchHelper.Callback =
            CustomItemTouchHelper(object : ItemTouchListener {
                override fun onMove(fromPosition: Int, toPosition: Int) {
                    lecturesAddedAdapter.onMove(fromPosition, toPosition)
                }

                override fun onSwipe(position: Int, direction: Int) {
                }
            })
        ItemTouchHelper(callback).attachToRecyclerView(holder.lectureList)

        holder.btnAddLecture.setOnClickListener {
            onAddLectureClick?.invoke(holder.bindingAdapterPosition)
            holder.lectureList.smoothScrollToPosition(lecturesAddedAdapter.itemCount - 1)
        }

        holder.lectureList.adapter = lecturesAddedAdapter
        holder.lectureList.layoutManager = LinearLayoutManager(context)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(sections, i, i + 1)
                Collections.swap(lectureAdapters, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(sections, i, i - 1)
                Collections.swap(lectureAdapters, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        if(fromPosition < toPosition)
            notifyItemRangeChanged(fromPosition, toPosition - fromPosition + 1)
        else
            notifyItemRangeChanged(toPosition, fromPosition - toPosition + 1)
    }
}