package com.deadlineshooters.yudemy.interfaces

interface ItemTouchListener {
    fun onMove(fromPosition: Int, toPosition: Int)
    fun onSwipe(position: Int, direction: Int)
}