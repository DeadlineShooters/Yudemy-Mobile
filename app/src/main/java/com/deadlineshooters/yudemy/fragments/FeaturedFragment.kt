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



        return view
    }
    override fun onClick(v: View?) {
        when (v?.id) {
//
        }
    }

}