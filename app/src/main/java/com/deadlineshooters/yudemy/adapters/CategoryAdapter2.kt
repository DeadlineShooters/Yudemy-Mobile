package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.google.android.material.button.MaterialButton

class CategoryAdapter2(private val categories: List<String>) :
    RecyclerView.Adapter<CategoryAdapter2.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)

        init {
            textView.setOnClickListener { onItemClick?.invoke(categories[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = categories[position]
    }

    override fun getItemCount(): Int = categories.size


}