package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseDetailActivity
import com.deadlineshooters.yudemy.activities.InstructorMainActivity
import com.deadlineshooters.yudemy.activities.SignInActivity


/**
 * A simple [Fragment] subclass.
 * Use the [FeaturedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeaturedFragment : Fragment(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_featured, container, false)

        view.findViewById<View>(R.id.btn_courseDetail).setOnClickListener(this)

        // test code for feedback popup
        val btnPopUpFeedback = view.findViewById<Button>(R.id.btn_popUpFeedback)
        btnPopUpFeedback.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.course_feedback_popup_layout) // replace 'your_layout' with the name of your layout file

            dialog.setCancelable(false) // Prevent the dialog from being dismissed when the user touches outside the dialog

            val closeButton = dialog.findViewById<ImageView>(R.id.iv_exit)
            closeButton.setOnClickListener {
                // Handle close button click here
                dialog.dismiss()
            }

            dialog.show()
        }
        val btnGoToInstructor = view.findViewById<Button>(R.id.btn_instructor)

        btnGoToInstructor.setOnClickListener {
            val intent = Intent(context, InstructorMainActivity::class.java)
            startActivity(intent)
        }


        return view
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_courseDetail -> {
                // Start CourseDetailActivity
                val intent = Intent(activity, CourseDetailActivity::class.java)
                startActivity(intent)
            }
        }
    }

}