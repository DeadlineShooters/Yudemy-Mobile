package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.google.android.material.button.MaterialButton

class CategoryAdapter1(private val categories: List<String>) :
    RecyclerView.Adapter<CategoryAdapter1.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: MaterialButton = itemView.findViewById(R.id.category_button)

        init {
            button.setOnClickListener { onItemClick?.invoke(categories[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter1.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button.text = categories[position]
    }

    override fun getItemCount(): Int = categories.size


}