package com.deadlineshooters.yudemy.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.android.highlighting.toSpannedString
import com.algolia.instantsearch.android.inflate
import com.algolia.instantsearch.core.hits.HitsView
import com.deadlineshooters.yudemy.models.Suggestion

class SuggestionAdapter(private val onSuggestionClick: ((Suggestion) -> Unit)) :
    ListAdapter<Suggestion, SuggestionAdapter.SuggestionViewHolder>(SuggestionAdapter),
    HitsView<Suggestion> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SuggestionViewHolder(parent.inflate(android.R.layout.simple_list_item_1))

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onSuggestionClick)
    }

    override fun setHits(hits: List<Suggestion>) = submitList(hits)

    class SuggestionViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Suggestion, onClick: ((Suggestion) -> Unit)) {
            view.setOnClickListener { onClick(item) }
            view.findViewById<TextView>(android.R.id.text1).text = item.highlightedQuery?.toSpannedString() ?: item.query
        }
    }

    companion object : DiffUtil.ItemCallback<Suggestion>() {
        override fun areItemsTheSame(oldItem: Suggestion, newItem: Suggestion) =
            oldItem.objectID == newItem.objectID

        override fun areContentsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean =
            oldItem == newItem
    }
}
