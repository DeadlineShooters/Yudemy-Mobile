package com.deadlineshooters.yudemy.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CreateCurriculumActivity
import com.deadlineshooters.yudemy.adapters.CourseAdapter
import com.deadlineshooters.yudemy.adapters.FeedbackAdapter
import com.deadlineshooters.yudemy.databinding.FragmentCourseDashboardBinding
import com.deadlineshooters.yudemy.databinding.FragmentFeedbackBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.google.android.material.bottomsheet.BottomSheetDialog


class CourseDashboardFragment : Fragment() {
    private lateinit var binding: FragmentCourseDashboardBinding
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCourseDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()

        // TODO: create course list
        var courseList: List<Course> = listOf(Course(), Course())

        courseAdapter = CourseAdapter(this, courseList)

        binding.rvCourses.adapter = courseAdapter
        binding.rvCourses.layoutManager = LinearLayoutManager(context)

        binding.ivFilter.setOnClickListener{
            val dialog = BottomSheetDialog(requireContext())
            val filterView = layoutInflater.inflate(R.layout.course_menu_bottom_sheet, null)
            dialog.setContentView(filterView)

            dialog.show()
        }

        binding.ivSearch.setOnClickListener{
            binding.llToolbar.visibility = View.GONE
            binding.llSearchBar.visibility = View.VISIBLE
        }

        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // TODO: implement performSearch()
//                performSearch()
                Log.d("CourseDashboard", "searching..")
                return@setOnEditorActionListener true
            }
            false
        }


        binding.ivCreateCourse.setOnClickListener{
//            replaceFragment(CourseUploadFragment())
            val intent = Intent(context, CreateCurriculumActivity::class.java)
            startActivity(intent)
        }

        binding.btnCancel.setOnClickListener {
            binding.llToolbar.visibility = View.VISIBLE
            binding.llSearchBar.visibility = View.GONE

            // Hide the keyboard
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setupActionBar() {
        val appCompatActivity = activity as AppCompatActivity?
        appCompatActivity?.setSupportActionBar(binding.toolbarFeedback)

    }

//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = requireActivity().supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, fragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
//    }

}