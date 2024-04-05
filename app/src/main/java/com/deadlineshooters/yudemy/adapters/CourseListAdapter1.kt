package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course
import org.w3c.dom.Text
import java.text.NumberFormat
import java.util.*

class CourseListAdapter1(context: Context, private val resource: Int, private val courses: List<Course>) :
    ArrayAdapter<Course>(context, resource, courses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val courseName: TextView = view.findViewById(R.id.courseName)
        val instructor: TextView = view.findViewById(R.id.instructor)
        val ratingNumber: TextView = view.findViewById(R.id.ratingNumber)
        val ratingStar: RatingBar = view.findViewById(R.id.ratingStar)
        val originalPrice: TextView = view.findViewById(R.id.originalPrice)
        val discountPrice: TextView = view.findViewById(R.id.discountPrice)
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        val course = courses[position]
        courseName.text = course.name
        instructor.text = course.instructor
        ratingNumber.text = course.avgRating.toString()
        ratingStar.setStepSize(0.5f);
        ratingStar.rating = course.avgRating.toFloat();
        originalPrice.text = currencyFormat.format(course.price.toInt())
        discountPrice.text = currencyFormat.format((course.price * 0.9).toInt())

        return view
    }
}

