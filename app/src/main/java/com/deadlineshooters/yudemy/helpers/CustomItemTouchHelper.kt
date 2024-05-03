package com.deadlineshooters.yudemy.helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.interfaces.ItemTouchListener


class CustomItemTouchHelper(private var mListener: ItemTouchListener?) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mListener?.onMove(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mListener?.onSwipe(viewHolder.bindingAdapterPosition, direction)
    }
}