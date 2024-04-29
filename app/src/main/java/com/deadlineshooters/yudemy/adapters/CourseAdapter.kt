package com.deadlineshooters.yudemy.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseRevenueAnalyticsActivity
import com.deadlineshooters.yudemy.activities.EditCourseLandingPageActivity
import com.deadlineshooters.yudemy.fragments.CourseDashboardFragment
import com.deadlineshooters.yudemy.fragments.CourseDraftingMenuFragment
import com.deadlineshooters.yudemy.helpers.FragmentHelper
import com.deadlineshooters.yudemy.helpers.StringUtils
import com.deadlineshooters.yudemy.models.Course
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.NumberFormat
import java.util.Locale


public class CourseAdapter(private val fragmentContext: CourseDashboardFragment, private var courseList: List<Course>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {
    var listener: AdapterView.OnItemClickListener? = null
    var onEditCourseClick: (() -> Unit)? = null
    var onDeleteCourseClick: ((Int) -> Unit)? = null
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))


    fun getCourseAt(position: Int): Course {
        return courseList[position]
    }
    fun updateCourses(newCourses: List<Course>) {
        courseList = newCourses

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val image_thumbnail: ImageView = itemView.findViewById(R.id.image_thumbnail)
        val text_video_title: TextView = itemView.findViewById(R.id.text_video_title)
        val tv_status: TextView = itemView.findViewById(R.id.tv_status)
        val tv_price: TextView = itemView.findViewById(R.id.tv_price_courseItem)
         val tv_rating: TextView = itemView.findViewById(R.id.tv_rating)
         val tv_totalEarning: TextView = itemView.findViewById(R.id.tv_totalEarning)
        val ivDot: ImageView = itemView.findViewById(R.id.iv_dot)
        val ll_rating: LinearLayout = itemView.findViewById(R.id.ll_rating)
        val tv_dot: TextView = itemView.findViewById(R.id.tv_dot)

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(null, v, position, itemId)
            }
        }

        init {
            ivDot.setOnClickListener {
                val context = itemView.context
                val dialog = BottomSheetDialog(context)
                val layoutInflater = LayoutInflater.from(context)
                val filterView = layoutInflater.inflate(R.layout.course_menu_bottom_sheet, null)
                dialog.setContentView(filterView)

                dialog.show()

                val llEdit = filterView.findViewById<LinearLayout>(R.id.ll_edit)
                val llDelete = filterView.findViewById<LinearLayout>(R.id.ll_delete)
                val llRevenueAnalytics = filterView.findViewById<LinearLayout>(R.id.ll_revenueAnalytics)

                val course = courseList[bindingAdapterPosition]

                llEdit.setOnClickListener {
                    FragmentHelper.replaceFragment(CourseDraftingMenuFragment(), course, fragmentContext)

                    dialog.dismiss()
                }

                llDelete.setOnClickListener {
                    onDeleteCourseClick?.invoke(bindingAdapterPosition)

                    dialog.dismiss()
                }

                llRevenueAnalytics.setOnClickListener {
                    dialog.dismiss()

                    val intent = Intent(context, CourseRevenueAnalyticsActivity::class.java)
                    intent.putExtra("course", course)

                    context.startActivity(intent)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(fragmentContext.context)
        val view = inflater.inflate(R.layout.course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courseList[position]

        // TODO: Fetch real data
        Glide.with(holder.itemView.context)
            .load(course.thumbnail.secure_url)
            .placeholder(R.drawable.placeholder)
            .into(holder.image_thumbnail)

        holder.text_video_title.text = course.name
        if (course.status == true) {
            holder.tv_status.text = "LIVE"
            holder.ll_rating.visibility = View.VISIBLE
            holder.tv_dot.visibility = View.VISIBLE
        } else {
            holder.tv_status.text = "DRAFT"
            holder.ll_rating.visibility = View.GONE
            holder.tv_dot.visibility = View.GONE
        }
        holder.tv_price.text = currencyFormat.format(course.price)


        holder.tv_rating.text = course.avgRating.toString()
        holder.tv_totalEarning.text = currencyFormat.format(course.totalRevenue)
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}
