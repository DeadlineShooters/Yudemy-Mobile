package com.deadlineshooters.yudemy.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CreateCurriculumActivity
import com.deadlineshooters.yudemy.activities.EditCourseLandingPageActivity
import com.deadlineshooters.yudemy.activities.PricingCourseDraftingActivity
import com.deadlineshooters.yudemy.databinding.FragmentCourseDraftingMenuBinding
import com.deadlineshooters.yudemy.helpers.DialogHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository

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
        binding.switchPublishCourse.isChecked = course!!.status

        updateSwitchPublish()

        binding.courseStatus.text = if(course!!.status) "Published" else "Draft"

        binding.tvCourseTitle.text = course?.name ?: ""
        Glide.with(this)
            .load(course?.thumbnail?.secure_url)
            .placeholder(R.drawable.placeholder)
            .into(binding.crsImgView)

        binding.navCurriculum.setOnClickListener {
            val intent = Intent(activity, CreateCurriculumActivity::class.java)
            intent.putExtra("course", course)
            startActivity(intent)
        }
        binding.navLandingPage.setOnClickListener {
            val intent = Intent(activity, EditCourseLandingPageActivity::class.java)
            intent.putExtra("course", course)
            startActivity(intent)
        }
        binding.navPricing.setOnClickListener {
            val intent = Intent(activity, PricingCourseDraftingActivity::class.java)
            intent.putExtra("course", course)
            startActivity(intent)
        }
        binding.switchPublishCourse.setOnCheckedChangeListener { _, isChecked ->
            course!!.status = isChecked
            CourseRepository().updateCourseStatus(course!!.id, isChecked)
                .addOnSuccessListener {
                    updateSwitchPublish()
                    binding.courseStatus.text = if(course!!.status) "Published" else "Draft"
                }
        }
    }

    override fun onResume() {
        super.onResume()
        DialogHelper.showProgressDialog(requireContext(), "Loading...")
        // Fetch the latest course information from the database
        course?.let { courseNonNull ->
            val prevStatus = courseNonNull.status
            CourseRepository().getCourseById(courseNonNull.id) { updatedCourse ->
                if (updatedCourse != null) {
                    course = updatedCourse
                    // Update UI with the latest course data
                    binding.tvCourseTitle.text = updatedCourse.name
                    Glide.with(this)
                        .load(updatedCourse.thumbnail.secure_url)
                        .placeholder(R.drawable.placeholder)
                        .into(binding.crsImgView)
                    // Update other UI elements as needed
                } else {
                    // Handle the case where the course data could not be fetched
                    Toast.makeText(context, "Failed to load course data.", Toast.LENGTH_SHORT).show()
                }
                DialogHelper.hideProgressDialog()
                if(prevStatus && !checkIfCanPublish()) {
                    binding.switchPublishCourse.isChecked = false
                    CourseRepository().updateCourseStatus(course!!.id, false)
                        .addOnSuccessListener {
                            updateSwitchPublish()
                            binding.courseStatus.text = "Draft"
                        }
                }
                else
                    updateSwitchPublish()
            }
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

    private fun checkIfCanPublish(): Boolean {
        return course!!.sectionList.isNotEmpty() && course!!.name != "" && course!!.description != "" && course!!.introduction != "" && course!!.thumbnail.secure_url != "" && course!!.promotionalVideo.secure_url != ""
    }

    private fun updateSwitchPublish() {
        binding.switchPublishCourse.isChecked = course!!.status
        binding.switchPublishCourse.isEnabled = checkIfCanPublish()
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