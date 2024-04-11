package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.CourseListAdapter2
import com.deadlineshooters.yudemy.adapters.InstructorListAdapter
import com.deadlineshooters.yudemy.databinding.FragmentFeaturedCategoryBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeaturedCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeaturedCategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var instructorViewModel: InstructorViewModel
    private lateinit var category: String
    private var _binding: FragmentFeaturedCategoryBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturedCategoryBinding.inflate(inflater, container, false)
        // Inflate the layout containing the RecyclerView
        val view = inflater.inflate(R.layout.fragment_featured_category, container, false)
        category = arguments?.getString("category")!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoryName.text = category

        instructorViewModel = ViewModelProvider(this).get(InstructorViewModel::class.java)
        instructorViewModel.instructors.observe(viewLifecycleOwner, Observer { instructors ->
            val adapter = InstructorListAdapter(instructors)
            binding.instructorList.adapter = adapter
            binding.instructorList.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        })

        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        courseViewModel.refreshCourses()
        courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            val adapter = CourseListAdapter2(requireContext(), courses)
            binding.courseList.layoutManager = LinearLayoutManager(context)
            binding.courseList.adapter = adapter
        })

        binding.backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FeaturedCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeaturedCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}