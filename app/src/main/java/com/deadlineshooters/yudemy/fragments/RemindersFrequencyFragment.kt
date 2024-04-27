package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.DayFrequencyAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RemindersFrequencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RemindersFrequencyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvDay: RecyclerView
    private lateinit var rvTime: RecyclerView
    private lateinit var backFromFrequency: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminders_frequency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDay = view.findViewById(R.id.rvDay)
        rvTime = view.findViewById(R.id.rvTime)
        backFromFrequency = view.findViewById(R.id.backFromFrequency)

        backFromFrequency.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val days = arrayListOf(1, 2)
        val dayAdapter = DayFrequencyAdapter(resources.getStringArray(R.array.frequency_day), null, days)
        rvDay.adapter = dayAdapter
        rvDay.layoutManager = LinearLayoutManager(activity)
        dayAdapter.onItemClick = fun(it: Int) {
            if(days.size == 1 && days.contains(it))
                return
            if(days.contains(it)) {
                days.remove(it)
                dayAdapter.notifyItemChanged(it)
            }
            else {
                days.add(it)
                dayAdapter.notifyItemChanged(it)
            }
        }

        val times = arrayListOf(1, 2)
        val timeAdapter = DayFrequencyAdapter(resources.getStringArray(R.array.frequency_time), resources.getStringArray(R.array.frequency_time_detail), times)
        rvTime.adapter = timeAdapter
        rvTime.layoutManager = LinearLayoutManager(activity)
        timeAdapter.onItemClick = fun(it: Int) {
            if(times.size == 1 && times.contains(it))
                return
            if(times.contains(it)) {
                times.remove(it)
                timeAdapter.notifyItemChanged(it)
            }
            else {
                times.add(it)
                timeAdapter.notifyItemChanged(it)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RemindersFrequencyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RemindersFrequencyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}