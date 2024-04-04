package com.deadlineshooters.yudemy.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseLearningActivity
import com.deadlineshooters.yudemy.adapters.MyLearningAdapter
import com.deadlineshooters.yudemy.adapters.BottomSheetDialogAdapter
import com.deadlineshooters.yudemy.databinding.FragmentLectureLearningBinding
import com.deadlineshooters.yudemy.databinding.FragmentMyLearningBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.viewmodels.CourseProgressViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyLearningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyLearningFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var filterDialog: BottomSheetDialog? = null

    private lateinit var binding: FragmentMyLearningBinding

    private lateinit var courseProgressViewModel: CourseProgressViewModel

    private var selectedCourseIdx: Int = 0
    private var selectedCourse: Course? = null

    private lateinit var myLearningAdapter: MyLearningAdapter

    private var filterIdx = 0
    private var searchQuery = ""

    private var updatedCourse: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        courseProgressViewModel = ViewModelProvider(this)[CourseProgressViewModel::class.java]
        courseProgressViewModel.getUserCourses()

        myLearningAdapter = MyLearningAdapter(arrayListOf(), arrayListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyLearningBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterDialog = createFilterDialog()

        courseProgressViewModel.combinedData.observe(viewLifecycleOwner, Observer { (courses, progresses) ->
            myLearningAdapter.refreshCourses(courses, progresses as ArrayList<Int>)
            if(filterIdx == 0) {
                courseProgressViewModel.myFavoriteCourseIds.observe(viewLifecycleOwner, Observer { courseIds ->
                    myLearningAdapter.filterFavoriteCourses(courseIds)
                })
            }
            if(searchQuery != "") {
                myLearningAdapter.searchCourses(searchQuery)
            }
        })

        binding.rvCourses.adapter = myLearningAdapter
        binding.rvCourses.layoutManager = LinearLayoutManager(activity)
        myLearningAdapter.onItemClick = { position, course, instructorName, progress ->
            selectedCourseIdx = position
            selectedCourse = course

            val intent = Intent(activity, CourseLearningActivity::class.java)
            intent.putExtra("course", course)
            intent.putExtra("instructorName", instructorName)
            intent.putExtra("progress", Math.toIntExact(progress.toLong()))
            startForResult.launch(intent)
        }

        // Click to search icon
        view.findViewById<Button>(R.id.searchBtn).setOnClickListener {
            showSearchAction()

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchQuery = query!!
                    refreshSearchList()

                    myLearningAdapter.searchCourses(searchQuery)
                    if(myLearningAdapter.courses.isEmpty()) {
                        binding.noResultLayout.visibility = View.VISIBLE
                    }
                    else {
                        binding.noResultLayout.visibility = View.GONE
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        view.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            searchQuery = ""
            refreshSearchList()
            hideSearchAction()
        }

        binding.rvCourses.setOnTouchListener { v, event ->
            if(searchQuery == "")
                hideSearchAction()
            false
        }

        // Click to filter icon
        view.findViewById<Button>(R.id.filterBtn).setOnClickListener {
            filterDialog!!.show()
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            val isUpdateProgress = data!!.getBooleanExtra("isUpdateProgress", true)
            if(isUpdateProgress) {
                courseProgressViewModel.refreshCourseProgress(selectedCourseIdx, selectedCourse!!)
                updatedCourse = selectedCourse
            }
        }
        else
            Log.d("LectureLearningFragment", "onActivityResult: canceled")
    }

    private fun showSearchAction() {
        requireView().findViewById<TextView>(R.id.frmTitle).visibility = View.GONE
        requireView().findViewById<LinearLayout>(R.id.actionLayout).visibility = View.GONE
        requireView().findViewById<ConstraintLayout>(R.id.searchLayout).visibility = View.VISIBLE
        val layoutParams = binding.rvCourses.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = R.id.searchLayout
        // focus search view and show keyboard
        binding.searchView.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchView, 0)
    }

    private fun hideSearchAction() {
        requireView().findViewById<TextView>(R.id.frmTitle).visibility = View.VISIBLE
        requireView().findViewById<LinearLayout>(R.id.actionLayout).visibility = View.VISIBLE
        requireView().findViewById<ConstraintLayout>(R.id.searchLayout).visibility = View.GONE
        val layoutParams = binding.rvCourses.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = R.id.frmTitle
        // clear text in search view
        binding.searchView.setQuery("", false)
    }

    private fun createFilterDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        val adapter = BottomSheetDialogAdapter(resources.getStringArray(R.array.my_learning_filter).toCollection(ArrayList()))
        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter, filterIdx ->
            if(filterIdx == 0) {
                courseProgressViewModel.getUserFavoriteCourseIds()
                courseProgressViewModel.myFavoriteCourseIds.observe(viewLifecycleOwner, Observer { courseIds ->
                    myLearningAdapter.filterFavoriteCourses(courseIds)
                })

                binding.frmTitle.text = resources.getStringArray(R.array.my_learning_filter)[filterIdx]
            }
            else {
                myLearningAdapter.refreshCourses(courseProgressViewModel.mylearningCourses.value!!, courseProgressViewModel.myCoursesProgress.value!! as ArrayList<Int>)
                binding.frmTitle.text = resources.getString(R.string.my_learning)
            }
            dialog.dismiss()
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }

    fun refreshSearchList() {
        if(filterIdx == 0 && courseProgressViewModel.myFavoriteCourseIds.value != null) {
            myLearningAdapter.filterFavoriteCourses(courseProgressViewModel.myFavoriteCourseIds.value!!)
        }
        else
            myLearningAdapter.refreshCourses(courseProgressViewModel.mylearningCourses.value!!, courseProgressViewModel.myCoursesProgress.value!! as ArrayList<Int>)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyLearning.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyLearningFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}