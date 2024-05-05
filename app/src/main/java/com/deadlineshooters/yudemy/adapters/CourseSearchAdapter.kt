package com.deadlineshooters.yudemy.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.android.highlighting.toSpannedString
import com.algolia.instantsearch.android.inflate
import com.algolia.instantsearch.core.hits.HitsView
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.StringUtils
import com.deadlineshooters.yudemy.models.AlgoliaCourse
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class CourseSearchAdapter : ListAdapter<AlgoliaCourse, CourseSearchAdapter.CourseViewHolder>(CourseDiffUtil), HitsView<AlgoliaCourse> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val holder = CourseViewHolder(parent.inflate(R.layout.course_list_item))
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(getItem(position))
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun setHits(hits: List<AlgoliaCourse>) = submitList(hits)

    private var onItemClickListener: ((AlgoliaCourse) -> Unit)? = null

    fun setOnItemClickListener(listener: (AlgoliaCourse) -> Unit) {
        onItemClickListener = listener
    }

    class CourseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(course: AlgoliaCourse) {
            val courseName: TextView = view.findViewById(R.id.courseName)
            val instructor: TextView = view.findViewById(R.id.instructor)
            val ratingNumber: TextView = view.findViewById(R.id.ratingNumber)
            val ratingStar: RatingBar = view.findViewById(R.id.ratingStar)
            val ratingQuantity: TextView = view.findViewById(R.id.ratingQuantity)
            val originalPrice: TextView = view.findViewById(R.id.originalPrice)
            val discountPrice: TextView = view.findViewById(R.id.discountPrice)
            val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

            courseName.text = course.highlightedName?.toSpannedString() ?: course.name
            instructor.text = course.instructor
            ratingNumber.text = course.avgRating.toBigDecimal().setScale(1, RoundingMode.UP).toString()
            ratingStar.setStepSize(0.1f);
            ratingStar.rating = StringUtils.roundToOneDecimalPlace(course.avgRating).toFloat();
            ratingQuantity.text = ""
            if (course.price > 0) {
                originalPrice.text = currencyFormat.format(course.price.toInt())
                discountPrice.text = currencyFormat.format((course.price * 0.9).toInt())
            } else {
                originalPrice.visibility = View.GONE
                discountPrice.text = "Free"
            }

            Glide.with(view)
                .load(course.thumbnail)
                .into(thumbnail)


        }
    }

    private object CourseDiffUtil : DiffUtil.ItemCallback<AlgoliaCourse>() {
        override fun areItemsTheSame(oldItem: AlgoliaCourse, newItem: AlgoliaCourse) = oldItem.objectID == newItem.objectID
        override fun areContentsTheSame(oldItem: AlgoliaCourse, newItem: AlgoliaCourse) = oldItem == newItem
    }
}
