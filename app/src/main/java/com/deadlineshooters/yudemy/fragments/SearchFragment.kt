package com.deadlineshooters.yudemy.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val FILTER_REQUEST_CODE = 1000

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private val viewModel by viewModels<QuerySuggestionViewModel>()
    private val connection = ConnectionHandler()

    private val resultAdapter = CourseSearchAdapter()
    private var originalCourses: List<AlgoliaCourse> = listOf()

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

        viewModel.courseSearcher.response.subscribe { response ->
            val courses = response?.hits?.deserialize(AlgoliaCourse.serializer())
            if (courses != null) {
                originalCourses = courses
            }
        }


        // Observe suggestions
        searchBoxView.setText(searchView.query.toString())
        viewModel.suggestions.observe(viewLifecycleOwner) { searchBoxView.setText(it.query, true) }


// Old version without indexing
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
            val intent = Intent(activity, FilterActivity::class.java)
            startActivityForResult(intent, FILTER_REQUEST_CODE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILTER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val sortOption = data?.getStringExtra("sortOption")
            val priceOptions = data?.getStringArrayExtra("priceOptions")
            val languageOptions = data?.getStringArrayExtra("languageOptions")
            val ratingOptions = data?.getStringArrayExtra("ratingOptions")
            val durationOptions = data?.getStringArrayExtra("durationOptions")


            var courses: List<AlgoliaCourse> = originalCourses

            courses = courses.filter { course ->
                (priceOptions?.isEmpty() ?: true || priceOptions?.contains(if (course.price == 0) "Free" else "Paid") ?: true) &&
                        (languageOptions?.isEmpty() ?: true || languageOptions?.contains(course.language) ?: true) &&
                        (ratingOptions?.isEmpty() ?: true || ratingOptions?.any { rating ->
                            val ratingNumber = if (rating.contains("& up")) {
                                rating.split(" ")[0].toDouble()
                            } else {
                                rating.toDouble()
                            }
                            ratingNumber <= course.avgRating
                        } ?: true) &&
                        (durationOptions?.isEmpty() ?: true || durationOptions?.any { duration -> course.totalLength in duration.toSecondsRange() } ?: true)
            }


            println(courses)

            courses = when (sortOption) {
                "Ratings" -> courses.sortedByDescending { it.avgRating }
                "Newest" -> courses.sortedByDescending { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.createdDate) }
                else -> courses
            }
            resultAdapter.submitList(courses)
        }
    }

    private fun String.toSecondsRange(): IntRange {
        val hoursRange = when (this) {
            "0-1 Hours" -> 0..3599
            "1-3 Hours" -> 3600..10799
            "3-6 Hours" -> 10800..21599
            "6+ Hours" -> 21600..Int.MAX_VALUE
            else -> 0..Int.MAX_VALUE
        }
        return hoursRange
    }
}