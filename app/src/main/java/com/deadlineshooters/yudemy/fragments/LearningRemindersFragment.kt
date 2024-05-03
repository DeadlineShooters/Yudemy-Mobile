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
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.helpers.AlarmHelper
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_INSTRUCTOR = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [LearningRemindersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LearningRemindersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var isInstructor: Boolean? = null

    private lateinit var backFromReminders: Button
    private lateinit var frequency: TextView

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isInstructor = it.getBoolean(ARG_INSTRUCTOR)
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
            replaceFragment(RemindersFrequencyFragment(), isInstructor!!)
        }

        val alarmHelper = AlarmHelper(requireContext())
        userViewModel.isToggleReminder.observe(viewLifecycleOwner, Observer {
            if(it) {
                frequency.visibility = View.VISIBLE
                switchReminder.isChecked = true

                userViewModel.dayTimeCombinedData.observe(viewLifecycleOwner, Observer { (days, times) ->
                    for(day in days) {
                        for(time in times) {
                            val tmpDay = (activity as BaseActivity).getDayFromIdx(day)
                            val tmpTime = (activity as BaseActivity).getHourFromIdx(time)
                            Log.d("LearningRemindersFragment", "change days: $tmpDay, times: $tmpTime")

                            if(!alarmHelper.checkIfActive(tmpDay, tmpTime)) {
                                Log.d("LearningRemindersFragment", "init push noti: $tmpDay, $tmpTime")
                                alarmHelper.initRepeatingAlarm(Calendar.getInstance(), tmpDay, tmpTime)
                            }
                        }
                    }
                })
            }
            else {
                frequency.visibility = View.GONE
                switchReminder.isChecked = false

                userViewModel.dayTimeCombinedData.observe(viewLifecycleOwner, Observer { (days, times) ->
                    val tmpDays = days.map { (activity as BaseActivity).getDayFromIdx(it) }
                    val tmpTimes = times.map { (activity as BaseActivity).getHourFromIdx(it) }
                    alarmHelper.cancelAllAlarms(tmpDays, tmpTimes)
                })
            }
        })

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            userViewModel.toggleReminder(isChecked)
        }
    }

    private fun replaceFragment(fragment: Fragment, isInstructor: Boolean) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (!isInstructor){
            fragmentTransaction.replace(R.id.frameLayout, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else{
            fragmentTransaction.replace(R.id.frameLayoutInstructor, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
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
        fun newInstance(isInstructor: Boolean) =
            LearningRemindersFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_INSTRUCTOR, isInstructor)
                }
            }
    }
}