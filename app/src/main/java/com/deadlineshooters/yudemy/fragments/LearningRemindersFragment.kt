package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.AlarmHelper
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LearningRemindersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LearningRemindersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var backFromReminders: Button
    private lateinit var frequency: TextView

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userViewModel.checkIfReminderToggled()
        userViewModel.getReminderDays()
        userViewModel.getReminderTimes()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learning_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backFromReminders = view.findViewById(R.id.backFromReminders)
        frequency = view.findViewById(R.id.frequencyNav)
        val switchReminder = view.findViewById<SwitchCompat>(R.id.switchAllow)

        backFromReminders.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        frequency.setOnClickListener {
            replaceFragment(RemindersFrequencyFragment())
        }

        val alarmHelper = AlarmHelper(requireContext())
        userViewModel.isToggleReminder.observe(viewLifecycleOwner, Observer {
            if(it) {
                frequency.visibility = View.VISIBLE
                switchReminder.isChecked = true

                userViewModel.dayTimeCombinedData.observe(viewLifecycleOwner, Observer { (days, times) ->
                    Log.d("LearningRemindersFragment", "change days: $days, times: $times")
                    for(day in days) {
                        for(time in times) {
//                            val tmpDay = getDayFromIdx(day)
//                            val tmpTime = getTimesFromIdx(time)

                            val calendar = Calendar.getInstance()
                            calendar.apply {
                                set(Calendar.DAY_OF_WEEK, day)
                                set(Calendar.HOUR_OF_DAY, time)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            alarmHelper.initRepeatingAlarm(calendar, day, time)
                        }
                    }
                })
            }
            else {
                frequency.visibility = View.GONE
                switchReminder.isChecked = false

                userViewModel.dayTimeCombinedData.observe(viewLifecycleOwner, Observer { (days, times) ->
                    alarmHelper.cancelAllAlarms(days, times)
                })
            }
        })

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            userViewModel.toggleReminder(isChecked)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LearningRemindersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LearningRemindersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun getDayFromIdx(idx: Int): Int {
        return when(idx) {
            0 -> Calendar.MONDAY
            1 -> Calendar.TUESDAY
            2 -> Calendar.WEDNESDAY
            3 -> Calendar.THURSDAY
            4 -> Calendar.FRIDAY
            5 -> Calendar.SATURDAY
            6 -> Calendar.SUNDAY
            else -> -1
        }
    }

    fun getTimesFromIdx(idx: Int): Int {
        return when(idx) {
            0 -> 6
            1 -> 9
            2 -> 12
            3 -> 15
            4 -> 18
            5 -> 21
            else -> -1
        }
    }
}