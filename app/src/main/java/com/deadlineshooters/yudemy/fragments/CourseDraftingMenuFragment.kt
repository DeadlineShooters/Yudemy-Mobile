package com.deadlineshooters.yudemy.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CreateCurriculumActivity
import com.deadlineshooters.yudemy.activities.EditCourseLandingPageActivity
import com.deadlineshooters.yudemy.activities.PricingCourseDraftingActivity
import com.deadlineshooters.yudemy.databinding.FragmentCourseDraftingMenuBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.SectionWithLectures

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURSE = "course"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CourseDraftingMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseDraftingMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var course: Course? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCourseDraftingMenuBinding

    private var sectionWithLectures: ArrayList<SectionWithLectures> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_COURSE, Course::class.java)
            } else {
                it.getParcelable(ARG_COURSE)
            }
            param2 = it.getString(ARG_PARAM2)
        }

        if(course == null)
            course = Course()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCourseDraftingMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()

        binding.courseStatus.text = if(course!!.status) "Published" else "Draft"

        binding.tvCourseTitle.text = course?.name ?: ""
        Glide.with(this)
            .load(course?.thumbnail?.secure_url)
            .placeholder(R.drawable.placeholder)
            .into(binding.crsImgView)

        binding.navCurriculum.setOnClickListener {
            val intent = Intent(activity, CreateCurriculumActivity::class.java)
            intent.putExtra("course", course)
            intent.putParcelableArrayListExtra("sections", sectionWithLectures)
            startForResult.launch(intent)
        }
        binding.navLandingPage.setOnClickListener {
            val intent = Intent(activity, EditCourseLandingPageActivity::class.java)
            intent.putExtra("course", course)
            startActivity(intent)
        }
        binding.navPricing.setOnClickListener {
            val intent = Intent(activity, PricingCourseDraftingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbarCourseDraftingMenu)

        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarCourseDraftingMenu.setNavigationOnClickListener { activity.supportFragmentManager.popBackStack() }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            sectionWithLectures = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                data!!.getParcelableArrayListExtra("sections", SectionWithLectures::class.java) as ArrayList<SectionWithLectures>
            else
                data!!.getParcelableArrayListExtra<SectionWithLectures>("sections") as ArrayList<SectionWithLectures>

            course = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                data.getParcelableExtra("course", Course::class.java)!!
            else
                data.getParcelableExtra<Course>("course")!!

            Log.d("CourseDraftingMenuFragment", "onActivityResult: sectionWithLectures $sectionWithLectures")
        }
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
                    putString(ARG_COURSE, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}