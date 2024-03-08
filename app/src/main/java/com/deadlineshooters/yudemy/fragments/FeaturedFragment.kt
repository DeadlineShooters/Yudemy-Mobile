package com.deadlineshooters.yudemy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseDetailActivity


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