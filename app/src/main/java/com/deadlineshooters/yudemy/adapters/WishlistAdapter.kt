package com.deadlineshooters.yudemy.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course

class WishlistAdapter(context: Context, private val resource: Int, private val courses: List<Course>) :
    ArrayAdapter<Course>(context, resource, courses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val courseName: TextView = view.findViewById(R.id.courseName)

        val course = courses[position]
        courseName.text = course.name

        return view
    }
}
