package com.deadlineshooters.yudemy.adapters

import android.content.Context
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R

class DayFrequencyAdapter(private val items: Array<String>, private val subItems: Array<String>?, private val chosenDays: ArrayList<Int>): RecyclerView.Adapter<DayFrequencyAdapter.ViewHolder>() {
    var onItemClick: ((Int) -> Unit)? = null
    private lateinit var context: Context
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val text = listItemView.findViewById<TextView>(android.R.id.text1)
        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(bindingAdapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(courseView)
    }
    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        TextViewCompat.setTextAppearance(holder.text, R.style.FrequencyDayTime)

        if(subItems == null) {
            holder.text.text = item
        }
        else {
            val detail = subItems[position]

            val mSpannableString = SpannableString("$item\n$detail")
            val detailSpan = TextAppearanceSpan(context, R.style.FrequencyTimeDetail)
            mSpannableString.setSpan(detailSpan, item.length, item.length + detail.length + 1, 0)

            holder.text.text = mSpannableString
        }

        // SET DRAWABLE TINT
        // Load the drawable from resources
        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_check)
        // Tint the drawable
        val wrappedDrawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(wrappedDrawable, context.resources.getColor(R.color.primary_color, null))

        // check if the day is chosen
        if(chosenDays.contains(position)) {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(null, null, wrappedDrawable, null)
        }
        else {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }
}