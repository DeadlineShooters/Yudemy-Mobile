package com.deadlineshooters.yudemy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CreateCurriculumActivity
import com.deadlineshooters.yudemy.activities.EditCourseLandingPageActivity
import com.deadlineshooters.yudemy.activities.PricingCourseDraftingActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CourseDraftingMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseDraftingMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navCurriculum: TextView
    private lateinit var navCourseLandingPage: TextView
    private lateinit var navPricing: TextView
    private lateinit var toolbar: Toolbar

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
        return inflater.inflate(R.layout.fragment_course_drafting_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_course_drafting_menu)
        navCurriculum = view.findViewById(R.id.navCurriculum)
        navCourseLandingPage = view.findViewById(R.id.navLandingPage)
        navPricing = view.findViewById(R.id.navPricing)

        setupActionBar()

        navCurriculum.setOnClickListener {
            val intent = Intent(activity, CreateCurriculumActivity::class.java)
            startActivity(intent)
        }
        navCourseLandingPage.setOnClickListener {
            val intent = Intent(activity, EditCourseLandingPageActivity::class.java)
            startActivity(intent)
        }
        navPricing.setOnClickListener {
            val intent = Intent(activity, PricingCourseDraftingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { activity.supportFragmentManager.popBackStack() }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CourseDraftingMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CourseDraftingMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}