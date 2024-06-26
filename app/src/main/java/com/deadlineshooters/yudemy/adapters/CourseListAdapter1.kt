package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.StringUtils
import com.deadlineshooters.yudemy.models.Course
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class CourseListAdapter1(context: Context, private val resource: Int, private val courses: List<Course>) :
    ArrayAdapter<Course>(context, resource, courses) {
    private var onItemClickListener: ((Course) -> Unit)? = null

    // Define a method to set the click listener
    fun setOnItemClickListener(listener: (Course) -> Unit) {
        onItemClickListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val courseName: TextView = view.findViewById(R.id.courseName)
        val instructor: TextView = view.findViewById(R.id.instructor)
        val ratingNumber: TextView = view.findViewById(R.id.ratingNumber)
        val ratingStar: RatingBar = view.findViewById(R.id.ratingStar)
        val ratingQuantity: TextView = view.findViewById(R.id.ratingQuantity)
        val originalPrice: TextView = view.findViewById(R.id.originalPrice)
        val discountPrice: TextView = view.findViewById(R.id.discountPrice)
        val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        val course = courses[position]
        courseName.text = course.name
        instructor.text = course.instructor
        ratingNumber.text = course.avgRating.toBigDecimal().setScale(1, RoundingMode.UP).toString()
        ratingStar.setStepSize(0.1f);
        ratingStar.rating = StringUtils.roundToOneDecimalPlace(course.avgRating).toFloat();
        (course.oneStarCnt + course.twoStarCnt + course.threeStarCnt + course.fourStarCnt + course.fiveStarCnt).toString()
            .also { ratingQuantity.text = "($it)" }
        if (course.price > 0) {
            originalPrice.text = currencyFormat.format(course.price.toInt())
            discountPrice.text = currencyFormat.format((course.price * 0.9).toInt())
        } else {
            originalPrice.visibility = View.GONE
            discountPrice.text = "Free"
        }

        Glide.with(view)
            .load(course.thumbnail.secure_url)
            .into(thumbnail)

        view.setOnClickListener {
            onItemClickListener?.invoke(course)
        }

        return view
    }
}

