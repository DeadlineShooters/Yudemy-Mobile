package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.adapters.DayTimeFrequencyAdapter
import com.deadlineshooters.yudemy.helpers.AlarmHelper
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import java.util.Calendar
import kotlin.text.Typography.times

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

    private lateinit var userViewModel: UserViewModel

    private lateinit var dayAdapter: DayTimeFrequencyAdapter
    private lateinit var timeAdapter: DayTimeFrequencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userViewModel.getReminderDays()
        userViewModel.getReminderTimes()

        dayAdapter = DayTimeFrequencyAdapter(resources.getStringArray(R.array.frequency_day), null, arrayListOf())
        timeAdapter = DayTimeFrequencyAdapter(resources.getStringArray(R.array.frequency_time), resources.getStringArray(R.array.frequency_time_detail), arrayListOf())
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

        userViewModel.reminderDays.observe(viewLifecycleOwner, Observer {
            dayAdapter.chosenItems = it
            for(day in it) {
                dayAdapter.notifyItemChanged(day)
            }
        })

        userViewModel.reminderTimes.observe(viewLifecycleOwner, Observer {
            for(time in it) {
                (activity as BaseActivity).getIdxFromHour(time).let { idx -> timeAdapter.chosenItems.add(idx) }
            }
//            timeAdapter.chosenItems = it
            for(time in it) {
                timeAdapter.notifyItemChanged(time)
            }
        })

        rvDay.adapter = dayAdapter
        rvDay.layoutManager = LinearLayoutManager(activity)
        dayAdapter.onItemClick = fun(it: Int) {
            if(dayAdapter.chosenItems.size == 1 && dayAdapter.chosenItems.contains(it))
                return
            if(dayAdapter.chosenItems.contains(it)) {
                dayAdapter.chosenItems.remove(it)
                userViewModel.removeReminderDay(it)
                dayAdapter.notifyItemChanged(it)
            }
            else {
                dayAdapter.chosenItems.add(it)
                userViewModel.addReminderDay(it)
                dayAdapter.notifyItemChanged(it)
            }
        }

        rvTime.adapter = timeAdapter
        rvTime.layoutManager = LinearLayoutManager(activity)
        timeAdapter.onItemClick = fun(it: Int) {
            if(timeAdapter.chosenItems.size == 1 && timeAdapter.chosenItems.contains(it))
                return
            if(timeAdapter.chosenItems.contains(it)) {
                timeAdapter.chosenItems.remove(it)
                userViewModel.removeReminderTime((activity as BaseActivity).getHourFromIdx(it))
                timeAdapter.notifyItemChanged(it)
            }
            else {
                timeAdapter.chosenItems.add(it)
                userViewModel.addReminderTime((activity as BaseActivity).getHourFromIdx(it))
                timeAdapter.notifyItemChanged(it)
            }
        }
    }

    fun setPushNoti(day: Int, hour: Int) {
        val alarmHelper = AlarmHelper(requireContext())
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.DAY_OF_WEEK, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        alarmHelper.initRepeatingAlarm(calendar, day, hour)
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