package com.deadlineshooters.yudemy.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Instructor

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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    private val dumpBio = "Hi, I'm Brad and I've taught web development to countless coworkers and held training sessions for fortune 100 companies.\n\n" +
            "I also teach local night classes and run a somewhat popular web development tutorial YouTube channel named LearnWebCode.\n\n" +
            "I'm a front-end developer, designer, and educator. I've been building user interfaces for over a decade for the world's largest brands, international technology leaders, and national political campaigns.\n\n" +
            "I'm fortunate to enjoy the development work I do, but my true passion is helping people learn."
    private val dumpInst = Instructor("123", "Brad", "Schiff", "Web developer", 72087, 247011,dumpBio, Image("secure_url", "public_id"), "walletId", "walletName")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instructorFullName = view.findViewById(R.id.instructorFullName)
        instructorHeadline = view.findViewById(R.id.instructorHeadline)
        instructorBio = view.findViewById(R.id.instructorBio)
        cancelEditProfileBtn = view.findViewById(R.id.cancelEditImageBtn)
        saveEditProfileInstructorBtn = view.findViewById(R.id.saveEditProfileInstructorBtn)

        instructorFullName.text = "${dumpInst.firstName} ${dumpInst.lastName}"
        instructorHeadline.text = dumpInst.headline
        instructorBio.text = dumpInst.bio

        saveEditProfileInstructorBtn.setOnClickListener {
            //TODO: Save the edited profile
        }

        cancelEditProfileBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage("Are you sure you want to discard the changes?")
                .setTitle("Please Confirm")
                .setNegativeButton(Html.fromHtml("<font color='#00000FF'><b>Cancel</b></font>")) { dialog, which ->

                }
                .setPositiveButton(Html.fromHtml("<font color='#FF0000'><b>Discard</b></font>")) { dialog, which ->
                    replaceFragment(AccountFragment())
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
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