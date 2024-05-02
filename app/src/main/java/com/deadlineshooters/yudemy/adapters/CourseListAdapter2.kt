package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course
import java.text.NumberFormat
import java.util.*

class CourseListAdapter2(private val context: Context, private val courses: List<Course>) :
    RecyclerView.Adapter<CourseListAdapter2.CourseViewHolder>() {
    var onItemClick: ((Course) -> Unit)? = null

    inner class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.courseName)
        val instructor: TextView = view.findViewById(R.id.instructor)
        val ratingNumber: TextView = view.findViewById(R.id.ratingNumber)
        val ratingQuantity: TextView = view.findViewById(R.id.ratingQuantity)
        val ratingStar: RatingBar = view.findViewById(R.id.ratingStar)
        val originalPrice: TextView = view.findViewById(R.id.originalPrice)
        val discountPrice: TextView = view.findViewById(R.id.discountPrice)
        val thumbnail: ImageView = view.findViewById(R.id.thumbnail)

        init {
            view.setOnClickListener { onItemClick?.invoke(courses[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_list_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.courseName.text = course.name
        holder.instructor.text = course.instructor
        holder.ratingNumber.text = course.avgRating.toString()
        holder.ratingStar.setStepSize(0.1f);
        holder.ratingStar.rating = course.avgRating.toFloat();
        (course.oneStarCnt + course.twoStarCnt + course.threeStarCnt + course.fourStarCnt + course.fiveStarCnt).toString()
            .also { holder.ratingQuantity.text = "($it)" }
        if (course.price > 0) {
            holder.originalPrice.text = currencyFormat.format(course.price.toInt())
            holder.discountPrice.text = currencyFormat.format((course.price * 0.9).toInt())
        } else {
            holder.originalPrice.visibility = GONE
            holder.discountPrice.text = "Free"
        }

        Glide.with(context)
            .load(course.thumbnail.secure_url)
            .into(holder.thumbnail)

    }

    override fun getItemCount() = courses.size
}
