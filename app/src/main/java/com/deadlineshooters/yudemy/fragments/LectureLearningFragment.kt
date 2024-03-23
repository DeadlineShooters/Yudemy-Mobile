package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.CourseLearningAdapter
import com.deadlineshooters.yudemy.databinding.FragmentCourseDashboardBinding
import com.deadlineshooters.yudemy.databinding.FragmentLectureLearningBinding
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.LectureViewModel
import com.deadlineshooters.yudemy.viewmodels.SectionViewModel
import com.deadlineshooters.yudemy.viewmodels.UserLectureViewModel
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURSE_ID = "courseId"

/**
 * A simple [Fragment] subclass.
 * Use the [LectureLearningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LectureLearningFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var courseId: String = ""
    private lateinit var rvSections: RecyclerView

    private lateinit var binding: FragmentLectureLearningBinding
    private lateinit var sectionViewModel: SectionViewModel
    private lateinit var userLectureViewModel: UserLectureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            courseId = it.getString(ARG_COURSE_ID)
            courseId = "2tNxr8j5FosEueZrL3wH"
        }

        sectionViewModel = ViewModelProvider(this)[SectionViewModel::class.java]
        sectionViewModel.getSectionsCourseLearning(courseId)

        userLectureViewModel = ViewModelProvider(this)[UserLectureViewModel::class.java]
        userLectureViewModel.getUserLecturesByCourse("pQ7PAicEnDck3dBL8uIGZgKcUXM2", courseId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLectureLearningBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSections = view.findViewById(R.id.rvSections)

        sectionViewModel.sectionsCourseLearning.observe(viewLifecycleOwner, Observer { sectionList ->
            userLectureViewModel.userLectures.observe(viewLifecycleOwner, Observer { userLectures ->
                val courseLearningAdapter = CourseLearningAdapter(sectionList, userLectures)
                rvSections.adapter = courseLearningAdapter
                rvSections.layoutManager = LinearLayoutManager(activity)
                courseLearningAdapter.onItemClick = { userLecture ->
                    Log.d("LectureLearningFragment", "User Lecture: $userLecture")
                }
            })
        })
    }

    fun createDummyData(): ArrayList<Section> {
        val sections = ArrayList<Section>()
        for(i in 1..5) {
            val l = Section("1", "Introduction", 1)
            sections.add(l)
        }
        return sections
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param courseId Course ID.
         * @return A new instance of fragment LectureLearningFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(courseId: String) =
            LectureLearningFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, courseId)
                }
            }
    }
}