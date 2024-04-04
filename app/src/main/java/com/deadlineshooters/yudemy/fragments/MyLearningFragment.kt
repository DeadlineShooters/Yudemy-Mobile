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
    private var searchView: SearchView? = null
    private var rvCourses: RecyclerView? = null
    private var filterDialog: BottomSheetDialog? = null

    private lateinit var courseProgressViewModel: CourseProgressViewModel

    private var selectedCourseIdx: Int = 0
    private var selectedCourse: Course? = null

    private lateinit var myLearningAdapter: MyLearningAdapter

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

        return inflater.inflate(R.layout.fragment_my_learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.searchView)
        rvCourses = view.findViewById(R.id.rvCourses)
        filterDialog = createFilterDialog()

        courseProgressViewModel.combinedData.observe(viewLifecycleOwner, Observer { (courses, progresses) ->
            Log.d("MyLearningFragment", "observe change: $courses, $progresses")

            myLearningAdapter.courses = courses
            myLearningAdapter.progresses = progresses as ArrayList<Int>

            myLearningAdapter.notifyDataSetChanged()
        })

        rvCourses!!.adapter = myLearningAdapter
        rvCourses!!.layoutManager = LinearLayoutManager(activity)
        myLearningAdapter.onItemClick = { position, course, instructorName, progress ->
            Log.d("MyLearningFragment", "onItemClick: $position, $course, $instructorName, $progress")

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
        }
        view.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            hideSearchAction()
        }

        rvCourses!!.setOnTouchListener { v, event ->
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
            }
        }
        else
            Log.d("LectureLearningFragment", "onActivityResult: canceled")
    }

    private fun showSearchAction() {
        requireView().findViewById<TextView>(R.id.frmTitle).visibility = View.GONE
        requireView().findViewById<LinearLayout>(R.id.actionLayout).visibility = View.GONE
        requireView().findViewById<ConstraintLayout>(R.id.searchLayout).visibility = View.VISIBLE
        val layoutParams = rvCourses!!.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = R.id.searchLayout
        // focus search view and show keyboard
        searchView!!.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchView, 0)
    }

    private fun hideSearchAction() {
        requireView().findViewById<TextView>(R.id.frmTitle).visibility = View.VISIBLE
        requireView().findViewById<LinearLayout>(R.id.actionLayout).visibility = View.VISIBLE
        requireView().findViewById<ConstraintLayout>(R.id.searchLayout).visibility = View.GONE
        val layoutParams = rvCourses!!.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = R.id.frmTitle
        // clear text in search view
        searchView!!.setQuery("", false)
    }

    private fun createFilterDialog(): BottomSheetDialog { //, R.style.MyTransparentBottomSheetDialogTheme
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        val adapter = BottomSheetDialogAdapter(resources.getStringArray(R.array.my_learning_filter).toCollection(ArrayList()))
        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter, filterIdx ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
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