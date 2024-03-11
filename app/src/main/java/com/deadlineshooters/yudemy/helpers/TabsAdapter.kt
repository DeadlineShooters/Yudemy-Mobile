package com.deadlineshooters.yudemy.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class TabsAdapter(val fragments: List<Fragment>, val fragmentManager: FragmentManager, val lifeCycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifeCycle) {
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}