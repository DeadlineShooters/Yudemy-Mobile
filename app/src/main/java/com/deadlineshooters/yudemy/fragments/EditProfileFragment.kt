package com.deadlineshooters.yudemy.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var instructorFullName: TextView
    private lateinit var instructorHeadline: TextView
    private lateinit var instructorBio: TextView
    private lateinit var cancelEditProfileBtn: TextView
    private lateinit var saveEditProfileInstructorBtn: Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var instructorViewModel: InstructorViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instructorFullName = view.findViewById(R.id.instructorFullName)
        instructorHeadline = view.findViewById(R.id.instructorHeadline)
        instructorBio = view.findViewById(R.id.instructorBio)
        cancelEditProfileBtn = view.findViewById(R.id.cancelEditImageBtn)
        saveEditProfileInstructorBtn = view.findViewById(R.id.saveEditProfileInstructorBtn)

        userViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
            if(user.instructor != null){
                instructorFullName.text = user.fullName
                instructorHeadline.text = user.instructor!!.headline
                instructorBio.text = user.instructor!!.bio
            }
        })


        instructorFullName.doAfterTextChanged {
            if(checkNewInfo()){
                saveEditProfileInstructorBtn.isClickable = true
                saveEditProfileInstructorBtn.isEnabled = true
                saveEditProfileInstructorBtn.setBackgroundColor(Color.parseColor("#000000"))
            } else{
                saveEditProfileInstructorBtn.isClickable = false
                saveEditProfileInstructorBtn.isEnabled = false
                saveEditProfileInstructorBtn.setBackgroundColor(Color.parseColor("#808080"))
            }
        }

        instructorHeadline.doAfterTextChanged {
            if(checkNewInfo()){
                saveEditProfileInstructorBtn.isClickable = true
                saveEditProfileInstructorBtn.isEnabled = true
                saveEditProfileInstructorBtn.setBackgroundColor(Color.parseColor("#000000"))
            } else{
                saveEditProfileInstructorBtn.isClickable = false
                saveEditProfileInstructorBtn.isEnabled = false
                saveEditProfileInstructorBtn.setBackgroundColor(Color.parseColor("#808080"))
            }
        }

        instructorBio.doAfterTextChanged {
            if(checkNewInfo()){
                saveEditProfileInstructorBtn.isClickable = true
                saveEditProfileInstructorBtn.isEnabled = true
                saveEditProfileInstructorBtn.setBackgroundColor(Color.parseColor("#000000"))
            } else{
                saveEditProfileInstructorBtn.isClickable = false
                saveEditProfileInstructorBtn.isEnabled = false
                saveEditProfileInstructorBtn.setBackgroundColor(Color.parseColor("#808080"))
            }
        }

        saveEditProfileInstructorBtn.setOnClickListener {
            //TODO: Save the edited profile
            instructorViewModel.modifyInstructorProfile(BaseActivity().getCurrentUserID(), instructorFullName.text.toString(), instructorHeadline.text.toString(), instructorBio.text.toString())
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            userViewModel.getCurUser()
        }

        cancelEditProfileBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage("Are you sure you want to discard the changes?")
                .setTitle("Please Confirm")
                .setNegativeButton(Html.fromHtml("<font color='#5624D0'><b>Cancel</b></font>")) { dialog, which ->

                }
                .setPositiveButton(Html.fromHtml("<font color='#B32D0F'><b>Discard</b></font>")) { dialog, which ->
                    requireActivity().supportFragmentManager.popBackStack()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

//    private fun isNewInfoEmpty(): Boolean {
//        return instructorFullName.text.toString().isEmpty() || instructorHeadline.text.toString().isEmpty() || instructorBio.text.toString().isEmpty()
//    }
    //TODO: Check if the new info is the same as the old info (chua xong)
    private fun checkNewInfo(): Boolean {
        return (instructorFullName.text.toString() != userViewModel.userData.value?.fullName || instructorHeadline.text.toString() != userViewModel.userData.value?.instructor?.headline || instructorBio.text.toString() != userViewModel.userData.value?.instructor?.bio)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}