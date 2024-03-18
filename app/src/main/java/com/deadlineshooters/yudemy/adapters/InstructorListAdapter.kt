package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Instructor
import com.google.android.material.button.MaterialButton

class InstructorListAdapter(private val instructors: List<Instructor>) :
    RecyclerView.Adapter<InstructorListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.instructor_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 6

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}