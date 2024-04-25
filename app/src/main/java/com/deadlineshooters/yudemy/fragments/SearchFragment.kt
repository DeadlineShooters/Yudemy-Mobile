package com.deadlineshooters.yudemy.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.hits.connectHitsView
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.search.helper.deserialize
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.FilterActivity
import com.deadlineshooters.yudemy.adapters.*
import com.deadlineshooters.yudemy.databinding.FragmentSearchBinding
import com.deadlineshooters.yudemy.models.AlgoliaCourse
import com.deadlineshooters.yudemy.models.Suggestion
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.QuerySuggestionViewModel
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
    private val viewModel by viewModels<QuerySuggestionViewModel>()
    private val connection = ConnectionHandler()

    private lateinit var courseViewModel: CourseViewModel
    private val courseRepository = CourseRepository()
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

        topSearchAdapter.onItemClick = { category ->
            courseViewModel = ViewModelProvider(this@SearchFragment)[CourseViewModel::class.java]
            courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
//                val clonedCourses = List(10) { courses[0] }
                val clonedCourses = List(1) { courses[0] }
                val resultAdapter = CourseListAdapter2(requireContext(), clonedCourses)
                binding.resultList.adapter = resultAdapter
                binding.emptyFrame.visibility = View.GONE
                binding.resultList.visibility = View.VISIBLE
            })
        }

        binding.backBtn.setOnClickListener {
            binding.emptyFrame.visibility = View.VISIBLE
            binding.resultList.visibility = View.GONE
        }

        val categoryList = binding.categoryList
        val categoryAdapter = CategoryAdapter3(categories)
        categoryList.adapter = categoryAdapter
        categoryList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
        categoryList.addItemDecoration(FeaturedFragment.SpaceItemDecoration(8))

        categoryAdapter.onItemClick = { category ->
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

        val searchView = binding.searchView
        val searchBoxView = SearchBoxViewAppCompat(searchView)
        connection += viewModel.searchBox.connectView(searchBoxView)

        val suggestionAdapter = SuggestionAdapter { viewModel.suggestions.value = it }
        binding.suggestionList.layoutManager = LinearLayoutManager(requireContext())
        binding.suggestionList.adapter = suggestionAdapter
        connection += viewModel.suggestionSearcher.connectHitsView(suggestionAdapter) {
            it.hits.deserialize(Suggestion.serializer())
        }

        val resultAdapter = CourseSearchAdapter()
        binding.resultList.layoutManager = LinearLayoutManager(requireContext())
        binding.resultList.adapter = resultAdapter
        connection += viewModel.courseSearcher.connectHitsView(resultAdapter) {
            it.hits.deserialize(AlgoliaCourse.serializer())
        }

        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.suggestionSearcher.setQuery(searchView.query.toString())
                viewModel.suggestionSearcher.searchAsync()
                binding.emptyFrame.visibility = View.GONE
                binding.resultList.visibility = View.GONE
                binding.suggestionList.visibility = View.VISIBLE
            } else {
                viewModel.courseSearcher.setQuery(searchView.query.toString())
                viewModel.courseSearcher.searchAsync()
                binding.emptyFrame.visibility = View.GONE
                binding.suggestionList.visibility = View.GONE
                binding.resultList.visibility = View.VISIBLE
            }
        }

        // Observe suggestions
        searchBoxView.setText(searchView.query.toString())
        viewModel.suggestions.observe(viewLifecycleOwner) { searchBoxView.setText(it.query, true) }

//
//
//        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]
//
//        var courseNames = ArrayList<String>()
//        courseViewModel.refreshCourses()
//        courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
//            courseNames = courses.map { it.name } as ArrayList<String>
//        })


//        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
//        val to = intArrayOf(android.R.id.text1)
//        val cursorAdapter =
//            SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
//
//        searchView.suggestionsAdapter = cursorAdapter
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                println(query)
//                courseViewModel.refreshSearchResult(query)
//                courseViewModel.searchResult.observe(viewLifecycleOwner, Observer { courses ->
//                    println(courses)
//                    val resultAdapter = CourseListAdapter1(requireContext(), R.layout.course_list_item, courses)
//                    binding.resultList.adapter = resultAdapter
//                    binding.emptyFrame.visibility = View.GONE
//                    binding.resultList.visibility = View.VISIBLE
//                })
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
//                newText?.let { query ->
//                    courseNames.forEachIndexed { index, suggestion ->
//                        if (suggestion.contains(query, ignoreCase = true)) {
//                            val startIndex = suggestion.indexOf(query, ignoreCase = true)
//                            val endIndex = startIndex + query.length
//                            val nextSpaceIndex = suggestion.indexOf(" ", endIndex)
//                            val phrase = if (nextSpaceIndex != -1) {
//                                suggestion.substring(startIndex, nextSpaceIndex)
//                            } else {
//                                suggestion.substring(startIndex)
//                            }
//                            cursor.addRow(arrayOf(index, phrase))
//                        }
//                    }
//                }
//                cursorAdapter.changeCursor(cursor)
//                return true
//            }
//        })
//
//        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
//            override fun onSuggestionSelect(position: Int): Boolean {
//                return false
//            }
//
//            override fun onSuggestionClick(position: Int): Boolean {
//                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
//                val columnIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
//                if (columnIndex != -1) {
//                    val selection = cursor.getString(columnIndex)
//                    searchView.setQuery(selection, false)
//                    // Handle suggestion click here
//                } else {
//                    // Handle case where column doesn't exist
//                }
//                return true
//            }
//
//        })

        binding.filterBtn.setOnClickListener {
            val intent = Intent(context, FilterActivity::class.java)
            startActivity(intent)
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