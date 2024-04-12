package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseDetailActivity
import com.deadlineshooters.yudemy.activities.EnrolledActivity
import com.deadlineshooters.yudemy.activities.InstructorMainActivity
import com.deadlineshooters.yudemy.activities.SignInActivity
import com.deadlineshooters.yudemy.adapters.CategoryAdapter1
import com.deadlineshooters.yudemy.adapters.CourseListAdapter1
import com.deadlineshooters.yudemy.adapters.CourseListAdapter2
import com.deadlineshooters.yudemy.databinding.FragmentFeaturedBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [FeaturedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeaturedFragment : Fragment() {
    private lateinit var courseViewModel: CourseViewModel
    private var _binding: FragmentFeaturedBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeaturedBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_featured, container, false)

        binding.btnCourseDetail.setOnClickListener {
            val intent = Intent(activity, CourseDetailActivity::class.java)
            startActivity(intent)
        }

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

        binding.btnEnrolledCourse.setOnClickListener {
            val intent = Intent(activity, EnrolledActivity::class.java)
            startActivity(intent)
        }
        // Obtain a reference to the RecyclerView
        val recyclerView = binding.categoryButtonList // Replace with your RecyclerView's ID

        // Create sample data for demonstration
        val categories = listOf(
            "Development", "Business", "Office Productivity", "Design",
            "Marketing", "Photography & Video", "Teaching & Academics",
            "Finance & Accounting", "IT & Software", "Personal Development",
            "Lifestyle", "Health & Fitness", "Music"
        )

        // Create an instance of the CategoryAdapter
        val adapter = CategoryAdapter1(categories)
        adapter.onItemClick = { category ->
            val fragment = FeaturedCategoryFragment()
            val bundle = Bundle()
            bundle.putString("category", category)
            fragment.arguments = bundle
            val fragmentManager = activity?.supportFragmentManager
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.replace(R.id.frameLayout, fragment)
            fragmentTransaction?.commit()
        }

        // Set the adapter on the RecyclerView
        recyclerView.adapter = adapter

        // Set a horizontal LinearLayoutManager for horizontal scrolling
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        recyclerView.addItemDecoration(SpaceItemDecoration(8))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            val clonedCourses = List(1) { courses[0] }
            val adapter = CourseListAdapter2(clonedCourses)
            binding.courseList.layoutManager = LinearLayoutManager(context)
            binding.courseList.adapter = adapter
            adapter.onItemClick = {course ->
                val intent = Intent(activity, CourseDetailActivity::class.java)
                startActivity(intent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space
            }
        }
    }
}
