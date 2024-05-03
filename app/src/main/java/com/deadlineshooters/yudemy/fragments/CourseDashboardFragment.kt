package com.deadlineshooters.yudemy.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.adapters.CourseAdapter
import com.deadlineshooters.yudemy.databinding.FragmentCourseDashboardBinding
import com.deadlineshooters.yudemy.models.Category
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CategoryRepository
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class CourseDashboardFragment : Fragment() {
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var binding: FragmentCourseDashboardBinding
    private lateinit var courseAdapter: CourseAdapter
    private var sortNewest = true

    private var categories = arrayListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        courseViewModel.refreshCourses(UserRepository.getCurrentUserID())

        courseAdapter = CourseAdapter(this, arrayListOf())
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

        courseViewModel.dashboardCourses.observe(viewLifecycleOwner, Observer { courses ->
            courseAdapter.updateCourses(courses)

            binding.rvCourses.adapter = courseAdapter
            binding.rvCourses.layoutManager = LinearLayoutManager(context)
        })

        val dialog = BottomSheetDialog(requireContext())
        val filterView = layoutInflater.inflate(R.layout.course_sort_bottom_sheet, null)
        val radioGroup = filterView.findViewById<RadioGroup>(R.id.radioGroup)

        if (sortNewest) {
            radioGroup.check(R.id.rb_newest)
        } else {
            radioGroup.check(R.id.rb_oldest)
        }

        binding.ivFilter.setOnClickListener {

            dialog.setContentView(filterView)

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_newest -> {
                        courseViewModel.refreshCourses(UserRepository.getCurrentUserID())
                        sortNewest = true
                    }

                    R.id.rb_oldest -> {
                        courseViewModel.refreshCourses(UserRepository.getCurrentUserID(), false)
                        sortNewest = false

                    }
                }
            }

            dialog.show()
        }

        courseAdapter.onDeleteCourseClick = { position ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete course")
            builder.setMessage("Are you sure you want to delete this course?")
            builder.setPositiveButton(Html.fromHtml("<font color='#B32D0F'><b>Yes</b></font>")) { _, _ ->
                val course = courseAdapter.getCourseAt(position)
                courseViewModel.deleteCourse(course)
            }
            builder.setNegativeButton(Html.fromHtml("<font color='#5624D0'><b>No</b></font>")) { _, _ -> }
            builder.show()
        }

        binding.ivSearch.setOnClickListener {
            binding.llToolbar.visibility = View.GONE
            binding.llSearchBar.visibility = View.VISIBLE
        }

        /** Set up auto text view*/
        courseViewModel.dashboardCourses.observe(viewLifecycleOwner, Observer { courses ->
            var adapterAutoCompleteTV = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                courses.map { it.name })
            binding.searchBar.threshold = 1
            binding.searchBar.setAdapter(adapterAutoCompleteTV)

            binding.searchBar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // No action needed here
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // No action needed here
                }

                override fun afterTextChanged(s: Editable) {
                    val filteredCourses = courses.filter { course ->
                        course.name.contains(s.toString(), ignoreCase = true)
                    }

                    courseAdapter.updateCourses(filteredCourses)
                    updateAutoCompleteTextView(filteredCourses)
                }
            })

        })


        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                val searchText = binding.searchBar.text.toString()
                val filteredCourses = courseViewModel.dashboardCourses.value?.filter { course ->
                    course.name.contains(searchText, ignoreCase = true)
                }
                filteredCourses?.let {
                    courseAdapter.updateCourses(it)
                }
                true
            } else {
                false
            }
        }


        binding.ivCreateCourse.setOnClickListener {
            createCourseDialog()
        }

        binding.btnCancel.setOnClickListener {
            binding.llToolbar.visibility = View.VISIBLE
            binding.llSearchBar.visibility = View.GONE

            // Hide the keyboard
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)

            // clear filter
            courseViewModel.refreshCourses(UserRepository.getCurrentUserID(), sortNewest)

        }
    }

    override fun onResume() {
        super.onResume()

        courseViewModel.refreshCourses(UserRepository.getCurrentUserID(), sortNewest)
    }

    fun updateAutoCompleteTextView(newCourses: List<Course>) {
        lateinit var adapterAutoCompleteTV: ArrayAdapter<String>

        adapterAutoCompleteTV = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            newCourses.map { it.name })
        binding.searchBar.setAdapter(adapterAutoCompleteTV)
    }
    private fun setupActionBar() {
        val appCompatActivity = activity as AppCompatActivity?
        appCompatActivity?.setSupportActionBar(binding.toolbarFeedback)

    }


    fun replaceFragment(fragment: Fragment, course: Course? = null) {
        val bundle = Bundle()
        if (course != null) {
            bundle.putParcelable("course", course)
            fragment.arguments = bundle
        }

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutInstructor, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun createCourseDialog() {
        val builder = AlertDialog.Builder(context)

        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_create_new_course, null)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<TextView>(R.id.btnCloseDlgCreateCourse).setOnClickListener {
            dialog.dismiss()
        }

        val cateSpinner = dialogView.findViewById<Spinner>(R.id.spinnerSetCate)

        if(categories.isEmpty()) {
            CategoryRepository().getCategories().addOnSuccessListener {
                categories.addAll(it)
                setCategoryAdapterForSpinner(categories, cateSpinner)
            }
        }
        else {
            setCategoryAdapterForSpinner(categories, cateSpinner)
        }

        dialogView.findViewById<Button>(R.id.btnCreateCourse).setOnClickListener {
            val courseTitle = dialogView.findViewById<TextView>(R.id.etNewTitle)
            if(courseTitle.text.isBlank()){
                courseTitle.error = "Please enter your course title"
                return@setOnClickListener
            }

            val course = Course()
            course.name = courseTitle.text.toString()
            course.category = categories[cateSpinner.selectedItemPosition]._id

            (activity as BaseActivity).showProgressDialog("Creating course...")
            CourseRepository().addCourse(course).addOnSuccessListener {
                (activity as BaseActivity).hideProgressDialog()
                course.id = it
                replaceFragment(CourseDraftingMenuFragment(), course)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    fun setCategoryAdapterForSpinner(categories: List<Category>, spinner: Spinner) {
        val categoriesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories.map { cate -> cate.name })
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = categoriesAdapter
    }

}