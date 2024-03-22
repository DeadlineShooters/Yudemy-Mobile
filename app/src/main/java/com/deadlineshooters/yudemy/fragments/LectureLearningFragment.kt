package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.CourseLearningAdapter
import com.deadlineshooters.yudemy.databinding.FragmentCourseDashboardBinding
import com.deadlineshooters.yudemy.databinding.FragmentLectureLearningBinding
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.viewmodels.SectionViewModel
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
    private var courseId: String? = null
    private lateinit var rvSections: RecyclerView

    private lateinit var binding: FragmentLectureLearningBinding
    private val sectionViewModel: SectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_COURSE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_lecture_learning, container, false)
        binding = FragmentLectureLearningBinding.inflate(inflater, container, false)

        sectionViewModel.getSectionsCourseLearning("")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSections = view.findViewById(R.id.rvSections)

        sectionViewModel.sectionsCourseLearning.observe(viewLifecycleOwner) {
            val courseLearningAdapter = CourseLearningAdapter(it, "pQ7PAicEnDck3dBL8uIGZgKcUXM2")
            rvSections.adapter = courseLearningAdapter
            rvSections.layoutManager = LinearLayoutManager(activity)
            courseLearningAdapter.onItemClick = { userLecture ->
                // TODO: open lecture
            }
        }
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
        fun newInstance(courseId: String?) =
            LectureLearningFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, courseId)
                }
            }
    }
}