package com.deadlineshooters.yudemy.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.AboutUsActivity
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Image
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        val imageUrl = "https://t4.ftcdn.net/jpg/00/97/58/97/360_F_97589769_t45CqXyzjz0KXwoBZT9PRaWGHRk5hQqQ.jpg"
        ImageViewHelper().setImageViewFromUrl(Image(imageUrl, ""), avatar)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext(),  R.style.ThemeOverlay_MyApp_MaterialAlertDialog)
            .setMessage("Sign out from Yudemy?")
            .setPositiveButton("Sign out") { dialog, which ->
                // TODO: Implement sign out
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