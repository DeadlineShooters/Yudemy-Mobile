package com.deadlineshooters.yudemy.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.AboutUsActivity
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.activities.InstructorMainActivity
import com.deadlineshooters.yudemy.activities.SignInActivity
import com.deadlineshooters.yudemy.activities.StudentMainActivity
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import com.deadlineshooters.yudemy.repositories.UserRepository
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
    private var isInstructor: Boolean? = false
    private var curUserId: String? = null

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
    private lateinit var fullName: TextView
    private lateinit var userViewModel: UserViewModel
    private val curUserEmail = BaseActivity().getCurrentUserEmail()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isInstructor = it.getBoolean(ARG_PARAM1)
            curUserId = it.getString(ARG_PARAM2)
        }
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userViewModel.getCurUser()
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
        fullName = view.findViewById(R.id.fullName)
        accSecurity = view.findViewById(R.id.accSecurity)
        closeAcc = view.findViewById(R.id.closeAcc)
        aboutYudemy = view.findViewById(R.id.aboutUs)
        signOut = view.findViewById(R.id.signOut)
        editProfile = view.findViewById(R.id.editProfile)
        editImage = view.findViewById(R.id.editImage)

        userViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
            fullName.text = user.fullName

            Glide.with(view)
                .load(user.image.secure_url)
                .circleCrop()
                .into(avatar)
        })

        email.text = curUserEmail


        if(isInstructor == true) {
            navigateIns.text = "Switch to Student View"
        } else {
            navigateIns.text = "Switch to Instructor View"
        }

        navigateIns.setOnClickListener {
            if(isInstructor == true){
                val intent = Intent(context, StudentMainActivity::class.java)
                startActivity(intent)
            } else {
                userViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
                    if(user.instructor == null){
                        UserRepository().becomeInstructor()
                        Toast.makeText(context, "You are now an instructor", Toast.LENGTH_SHORT).show()
                    }
                })
                val intent = Intent(context, InstructorMainActivity::class.java)
                startActivity(intent)
            }
        }

        learningReminders.setOnClickListener {
            replaceFragment(LearningRemindersFragment.newInstance(isInstructor!!), isInstructor!!)
        }

        accSecurity.setOnClickListener {
            replaceFragment(AccountSecurityFragment(), isInstructor!!)
        }

        closeAcc.setOnClickListener {
            replaceFragment(CloseAccountFragment(), isInstructor!!)
        }

        aboutYudemy.setOnClickListener {
            val intent = Intent(context, AboutUsActivity::class.java)
            startActivity(intent)
        }

        signOut.setOnClickListener {
            showSignOutDialog()
        }

        if(isInstructor == true){
            editProfile.visibility = View.VISIBLE
            editImage.visibility = View.VISIBLE
        }

        editProfile.setOnClickListener {
            replaceFragment(EditProfileFragment(), isInstructor!!)
        }

        editImage.setOnClickListener {
            replaceFragment(EditImageFragment(), isInstructor!!)
        }

        UserRepository().loadUserData(this@AccountFragment)
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

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext(),  R.style.ThemeOverlay_MyApp_MaterialAlertDialog)
            .setMessage("Sign out from Yudemy?")
            .setPositiveButton(Html.fromHtml("<font color='#B32D0F'><b>Sign Out</b></font>")) { dialog, which ->
                // TODO: Implement sign out
                AuthenticationRepository().signOut { isSignedOut ->
                    if (isSignedOut == true) {
                        val intent = Intent(context, SignInActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton(Html.fromHtml("<font color='#5624D0'><b>Cancel</b></font>")) { dialog, which ->
                // Do something else.
            }
            .show()
    }

    fun setUserData(user: User) {
        if (requireActivity() is StudentMainActivity) {
            if (user.instructor != null)
                navigateIns.text = "Switch to instructor view"
            else
                navigateIns.text = "Become an instructor"
        }
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
        fun newInstance(param1: Boolean, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}