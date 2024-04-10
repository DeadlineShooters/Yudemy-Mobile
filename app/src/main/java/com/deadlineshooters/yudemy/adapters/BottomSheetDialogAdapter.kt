package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R

class BottomSheetDialogAdapter(private val options: ArrayList<String>): RecyclerView.Adapter<BottomSheetDialogAdapter.ViewHolder>() {
    var onItemClick: ((String, Int) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val option = listItemView.findViewById<TextView>(R.id.filterOption)
        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(options[bindingAdapterPosition], bindingAdapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(R.layout.bottom_sheet_dialog_item, parent, false)
        return ViewHolder(courseView)
    }
    override fun getItemCount(): Int {
        return options.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.option.text = options[position]
    }
}