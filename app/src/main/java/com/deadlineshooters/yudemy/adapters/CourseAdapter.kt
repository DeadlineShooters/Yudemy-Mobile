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
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseRevenueAnalyticsActivity
import com.deadlineshooters.yudemy.activities.EditCourseLandingPageActivity
import com.deadlineshooters.yudemy.fragments.CourseDashboardFragment
import com.deadlineshooters.yudemy.fragments.CourseDraftingMenuFragment
import com.deadlineshooters.yudemy.models.Course
import com.google.android.material.bottomsheet.BottomSheetDialog


public class CourseAdapter(private val context: CourseDashboardFragment, private val courseList: List<Course>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {
    var listener: AdapterView.OnItemClickListener? = null
    var onEditCourseClick: (() -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val image_thumbnail: ImageView = itemView.findViewById(R.id.image_thumbnail)
        val text_video_title: TextView = itemView.findViewById(R.id.text_video_title)
        val tv_status: TextView = itemView.findViewById(R.id.tv_status)
        val tv_price: TextView = itemView.findViewById(R.id.tv_price_courseItem)
         val tv_rating: TextView = itemView.findViewById(R.id.tv_rating)
         val tv_totalEarning: TextView = itemView.findViewById(R.id.tv_totalEarning)
        val ivDot: ImageView = itemView.findViewById(R.id.iv_dot)

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

                llEdit.setOnClickListener {
                    // TODO: Navigate to edit course
                    onEditCourseClick?.invoke()

                    dialog.dismiss()
                }

                llDelete.setOnClickListener {
                    // TODO: Shows pop up delete course
                    Toast.makeText(context, "Delete clicked!", Toast.LENGTH_SHORT).show()
                }

                llRevenueAnalytics.setOnClickListener {
                    val intent = Intent(context, CourseRevenueAnalyticsActivity::class.java)
                    context.startActivity(intent)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context.context)
        val view = inflater.inflate(R.layout.course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courseList[position]

        // TODO: Fetch real data

        // TODO 1: Delete dummy data
        holder.image_thumbnail.setImageResource(R.drawable.ic_launcher_background)
        holder.text_video_title.text = "Node.js Absolute Beginner Guide - Learn Node From Scratch"
        holder.tv_status.text = "LIVE"
        holder.tv_price.text = "\$199.99"
        holder.tv_rating.text = "4.74"
        holder.tv_totalEarning.text = "$30.89"
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}
