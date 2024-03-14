package com.deadlineshooters.yudemy.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.FilterActivity
import com.deadlineshooters.yudemy.adapters.CategoryAdapter1
import com.deadlineshooters.yudemy.adapters.CategoryAdapter3
import com.deadlineshooters.yudemy.adapters.CourseListAdapter1
import com.deadlineshooters.yudemy.databinding.FragmentSearchBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private lateinit var courseViewModel: CourseViewModel
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories = listOf(
            "Development", "Business", "Office Productivity", "Design",
            "Marketing", "Photography & Video", "Teaching & Academics",
            "Finance & Accounting", "IT & Software", "Personal Development",
            "Lifestyle", "Health & Fitness", "Music"
        )

        val topSearchList = binding.topSearchList
        val topSearchAdapter = CategoryAdapter1(categories)
        topSearchList.adapter = topSearchAdapter

        // Set a FlexboxLayoutManager for wrapping content
        var layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        topSearchList.layoutManager = layoutManager
        topSearchList.addItemDecoration(FeaturedFragment.SpaceItemDecoration(8))

        val categoryList = binding.categoryList
        val categoryAdapter = CategoryAdapter3(categories)
        categoryList.adapter = categoryAdapter
        categoryList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
        categoryList.addItemDecoration(FeaturedFragment.SpaceItemDecoration(8))

        val searchView = binding.searchView
        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                courseViewModel = ViewModelProvider(this@SearchFragment).get(CourseViewModel::class.java)
                courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
                    val clonedCourses = List(10) { courses[0] }
                    val resultAdapter = CourseListAdapter1(requireContext(), R.layout.course_list_item, clonedCourses)
                    binding.resultList.adapter = resultAdapter
                    binding.emptyFrame.visibility = View.GONE
                    binding.resultList.visibility = View.VISIBLE
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // The query text has changed
                return true
            }
        })
        binding.filterBtn.setOnClickListener {
            val intent = Intent(context, FilterActivity::class.java)
            startActivity(intent)
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
         * @return A new instance of fragment Search.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}