package com.deadlineshooters.yudemy.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R

class MyLearningFilterAdapter(private val options: Array<String>): RecyclerView.Adapter<MyLearningFilterAdapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val option = listItemView.findViewById<TextView>(R.id.filterOption)
        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(options[absoluteAdapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyLearningFilterAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(R.layout.my_learning_filter_item, parent, false)
        return ViewHolder(courseView)
    }
    override fun getItemCount(): Int {
        return options.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.option.text = options[position]
    }
}