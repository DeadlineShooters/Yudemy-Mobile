package com.deadlineshooters.yudemy.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.hits.connectHitsView
import com.algolia.search.helper.deserialize
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseDetailActivity
import com.deadlineshooters.yudemy.adapters.CourseSearchAdapter
import com.deadlineshooters.yudemy.databinding.FragmentFeaturedCategoryBinding
import com.deadlineshooters.yudemy.models.AlgoliaCourse
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.QuerySuggestionViewModel

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
    private val viewModel by viewModels<QuerySuggestionViewModel>()
    private val connection = ConnectionHandler()
    private lateinit var category: String
    private var _binding: FragmentFeaturedCategoryBinding? = null
    private val binding get() = _binding!!
    private val courseRepository = CourseRepository()

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

//        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]
//        courseViewModel.refreshCourses()
//        courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
//            val adapter = CourseListAdapter2(requireContext(), courses)
//            binding.courseList.layoutManager = LinearLayoutManager(context)
//            binding.courseList.adapter = adapter
//        })

        val courseListAdapter = CourseSearchAdapter()
        binding.courseList.layoutManager = LinearLayoutManager(requireContext())
        binding.courseList.adapter = courseListAdapter
        courseListAdapter.setOnItemClickListener { course ->
            courseRepository.getCourseById(course.objectID.toString()) {courseDoc ->
                val intent = Intent(requireContext(), CourseDetailActivity::class.java)
                intent.putExtra("course", courseDoc)
                startActivity(intent)
            }
        }

        connection += viewModel.courseSearcher.connectHitsView(courseListAdapter) {
            it.hits.deserialize(AlgoliaCourse.serializer())
        }

        viewModel.courseSearcher.setQuery(category)
        viewModel.courseSearcher.searchAsync()

        binding.backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        viewModel.courseSearcher.response.subscribe { response ->
            val courses = response?.hits?.deserialize(AlgoliaCourse.serializer())
            if (courses != null) {
                if (courses.isEmpty()) {
                    binding.placeholder.visibility = VISIBLE
                    binding.placeholderTV.visibility = VISIBLE
                } else {
                    binding.placeholder.visibility = GONE
                    binding.placeholderTV.visibility = GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
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