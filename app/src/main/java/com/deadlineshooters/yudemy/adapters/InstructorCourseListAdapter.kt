package com.deadlineshooters.yudemy.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course

class InstructorCourseListAdapter(private val courseList: List<Course>): RecyclerView.Adapter<InstructorCourseListAdapter.ViewHolder>() {
    var onItemClick: ((Course) -> Unit)? = null

    inner class ViewHolder(listCourseView: View) : RecyclerView.ViewHolder(listCourseView) {
        val courseName: TextView = listCourseView.findViewById(R.id.courseName)
        val ratingNumber: TextView = listCourseView.findViewById(R.id.ratingNumber)
        val ratingStar: RatingBar = listCourseView.findViewById(R.id.ratingStar)
        val ratingAmount: TextView = listCourseView.findViewById(R.id.ratingAmount)
        val discountPrice: TextView = listCourseView.findViewById(R.id.discountPrice)
        val originalPrice: TextView = listCourseView.findViewById(R.id.originalPrice)
        val instructor: TextView = listCourseView.findViewById(R.id.instructor)

        init {
            listCourseView.setOnClickListener { onItemClick?.invoke(courseList[adapterPosition]) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructorCourseListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(R.layout.instructor_course_list_item, parent, false)
        return ViewHolder(courseView)
    }

    override fun onBindViewHolder(holder: InstructorCourseListAdapter.ViewHolder, position: Int) {
        val course: Course = courseList[position]


        val courseName = holder.courseName
        val ratingNumber = holder.ratingNumber
        val ratingStar = holder.ratingStar
        val ratingAmount = holder.ratingAmount
        val discountPrice = holder.discountPrice
        val originalPrice = holder.originalPrice
        val instructor = holder.instructor

        courseName.text = course.name
        ratingNumber.text = course.avgRating.toString()
        ratingStar.rating = course.avgRating.toFloat()
        ratingAmount.text = course.totalStudents.toString()
        discountPrice.text = course.price.toString()
        originalPrice.text = ((course.price*90)/100).toString()
        instructor.text = course.instructor

    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}