package com.deadlineshooters.yudemy.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.FeedbackAdapter
import com.deadlineshooters.yudemy.databinding.FragmentFeedbackBinding
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CourseFeedbackViewModel
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class FeedbackFragment : Fragment() {
    private lateinit var courseFeedbackViewModel: CourseFeedbackViewModel
    private lateinit var binding: FragmentFeedbackBinding
    private lateinit var feedbackAdapter: FeedbackAdapter
    private  var noInstructorResponse : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseFeedbackViewModel = ViewModelProvider(this)[CourseFeedbackViewModel::class.java]
        courseFeedbackViewModel.refreshCourseFeedbackForInstructor(noInstructorResponse)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()


        courseFeedbackViewModel.feedback_íntructor.observe(viewLifecycleOwner, Observer { feedbackList ->
            if (feedbackList.isNotEmpty()) {
                // Create the FeedbackAdapter with the feedbackList
                binding.tvNoFeedback.visibility = View.GONE

            } else {

                binding.tvNoFeedback.visibility = View.VISIBLE
            }

            feedbackAdapter = FeedbackAdapter(this, feedbackList)

            // Set the RecyclerView's adapter to your FeedbackAdapter
            binding.rvFeedback.adapter = feedbackAdapter
        })


        // Set the RecyclerView's layout manager
        binding.rvFeedback.layoutManager = LinearLayoutManager(context)

        binding.ivFilter.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val filterView = layoutInflater.inflate(R.layout.response_status_bottom_sheet, null)
            dialog.setContentView(filterView)

            val radioNotResponded = filterView.findViewById<RadioButton>(R.id.radioNotResponded)
            val radioResponded = filterView.findViewById<RadioButton>(R.id.radioResponded)
            val btnClearFilter = filterView.findViewById<Button>(R.id.btnClearFilter)

            radioNotResponded.setOnClickListener {
                // Change noInstructorResponse to true
                noInstructorResponse = true
                courseFeedbackViewModel.refreshCourseFeedbackForInstructor(noInstructorResponse)


            }

            radioResponded.setOnClickListener {
                // Change noInstructorResponse to false
                noInstructorResponse = false
                courseFeedbackViewModel.refreshCourseFeedbackForInstructor(noInstructorResponse)


            }

            btnClearFilter.setOnClickListener {
                // Do not pass the no response parameter
                noInstructorResponse = null
                courseFeedbackViewModel.refreshCourseFeedbackForInstructor(noInstructorResponse)

            }

            dialog.dismiss()
            dialog.show()
        }



        binding.btnCancel.setOnClickListener {
            binding.llToolbar.visibility = View.VISIBLE
            binding.llSearchBar.visibility = View.GONE

            // Hide the keyboard
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // Set up the search bar

        binding.searchBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (binding.searchBar.compoundDrawables[2] != null) {
                    if (event.rawX >= (binding.searchBar.right - binding.searchBar.compoundDrawables[2].bounds.width())) {
                        binding.searchBar.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }

        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // TODO: implement performSearch()
//                performSearch()
                Log.d("Feedback", "searching for ${binding.searchBar.text}")
                return@setOnEditorActionListener true
            }
            false
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    // No text -> show search icon only
                    binding.searchBar.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_outline,
                        0,
                        0,
                        0
                    )
                } else {
                    // Text entered -> show search icon and clear icon
                    binding.searchBar.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_outline,
                        0,
                        R.drawable.x_close,
                        0
                    )
                }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    override fun onResume() {
        super.onResume()

        courseFeedbackViewModel.refreshCourseFeedbackForInstructor(noInstructorResponse)

    }

    private fun setupActionBar() {
        val appCompatActivity = activity as AppCompatActivity?
        appCompatActivity?.setSupportActionBar(binding.toolbarFeedback)

    }

}