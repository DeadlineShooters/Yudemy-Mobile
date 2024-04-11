package com.deadlineshooters.yudemy.fragments

import NotificationHelper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.AboutUsActivity
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.activities.InstructorMainActivity
import com.deadlineshooters.yudemy.activities.SignInActivity
import com.deadlineshooters.yudemy.activities.StudentMainActivity
import com.deadlineshooters.yudemy.helpers.AlarmHelper
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var avatar: ImageView
    private lateinit var email: TextView
    private lateinit var navigateIns: TextView
    private lateinit var learningReminders: TextView
    private lateinit var accSecurity: TextView
    private lateinit var closeAcc: TextView
    private lateinit var aboutYudemy: TextView
    private lateinit var signOut: TextView
    private lateinit var editProfile: TextView
    private lateinit var editImage: TextView
    private val curUserEmail = BaseActivity().getCurrentUserEmail()

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
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        avatar = view.findViewById(R.id.avatar)
        email = view.findViewById(R.id.email)
        navigateIns = view.findViewById(R.id.navigateIns)
        learningReminders = view.findViewById(R.id.learningReminders)
        accSecurity = view.findViewById(R.id.accSecurity)
        closeAcc = view.findViewById(R.id.closeAcc)
        aboutYudemy = view.findViewById(R.id.aboutUs)
        signOut = view.findViewById(R.id.signOut)
        editProfile = view.findViewById(R.id.editProfile)
        editImage = view.findViewById(R.id.editImage)

        email.text = curUserEmail.toString()

        if (requireActivity() is InstructorMainActivity) {
            navigateIns.text = "Switch to Student View"
        } else {
            navigateIns.text = "Switch to Instructor View"
        }

        navigateIns.setOnClickListener {
            val curActivity = context

            val intent =when (curActivity) {
                is InstructorMainActivity -> Intent(context, StudentMainActivity::class.java)
                is StudentMainActivity -> Intent(context, InstructorMainActivity::class.java)
                else -> throw IllegalStateException("Unexpected activity: $curActivity")
            }
            startActivity(intent)
        }

        learningReminders.setOnClickListener {
            replaceFragment(LearningRemindersFragment())
        }

        accSecurity.setOnClickListener {
            replaceFragment(AccountSecurityFragment())
        }

        closeAcc.setOnClickListener {
            replaceFragment(CloseAccountFragment())
        }

        aboutYudemy.setOnClickListener {
            val intent = Intent(context, AboutUsActivity::class.java)
            startActivity(intent)
        }

        signOut.setOnClickListener {
            showSignOutDialog()
        }

        editProfile.setOnClickListener {
            replaceFragment(EditProfileFragment())
        }

        editImage.setOnClickListener {
            replaceFragment(EditImageFragment())
        }

        val imageUrl = "https://t4.ftcdn.net/jpg/00/97/58/97/360_F_97589769_t45CqXyzjz0KXwoBZT9PRaWGHRk5hQqQ.jpg"
        ImageViewHelper().setImageViewFromUrl(Image(imageUrl, ""), avatar)

//        val alarmScheduler = NotificationHelper(requireContext())
//        alarmScheduler.cancel(Calendar.THURSDAY, 1)
//        alarmScheduler.schedule(
//            Calendar.THURSDAY,
//            1)
//        alarmScheduler.cancel(Calendar.THURSDAY, 1, 30)
//        alarmScheduler.schedule(
//            Calendar.THURSDAY,
//            1, 30)

        // TODO: Uncomment this code to test the alarm
//        val calendar = Calendar.getInstance()
//        val alarmHelper = AlarmHelper(requireContext())
//        alarmHelper.cancelAlarm()
//        alarmHelper.initRepeatingAlarm(calendar, Calendar.THURSDAY, 9)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext(),  R.style.ThemeOverlay_MyApp_MaterialAlertDialog)
            .setMessage("Sign out from Yudemy?")
            .setPositiveButton("Sign out") { dialog, which ->
                // TODO: Implement sign out
                AuthenticationRepository().signOut { isSignedOut ->
                    if (isSignedOut == true) {
                        val intent = Intent(context, SignInActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Do something else.
            }
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}