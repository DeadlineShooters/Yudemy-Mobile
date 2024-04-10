package com.deadlineshooters.yudemy.fragments

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseLearningActivity
import com.deadlineshooters.yudemy.adapters.MyLearningAdapter
import com.deadlineshooters.yudemy.adapters.MyLearningFilterAdapter
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.Video
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        filterDialog =createFilterDialog()

        val courses = generateDummyData() // TODO: get user's courses from database
//        val userId = (activity as BaseActivity).getCurrentUserID() TODO: get current user id
        val adapter = MyLearningAdapter(courses, "userId")
        rvCourses!!.adapter = adapter
        rvCourses!!.layoutManager = LinearLayoutManager(activity)
        adapter.onItemClick = { course ->
            // TODO: open course learning

            val intent = Intent(activity, CourseLearningActivity::class.java)
            intent.putExtra("courseId", course.id)
            startActivity(intent)
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
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_my_learning_filter, null)
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        val adapter = MyLearningFilterAdapter(resources.getStringArray(R.array.my_learning_filter))
        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }

    fun generateDummyData(): ArrayList<Course> {
        val courses = ArrayList<Course>()
        for(i in 1..10) {
            val c = Course(
                name = "Graph Theory Algorithms for Competitive Programming (2022) $i",
                instructor = "34349",
                totalStudents = 0,
                introduction = "Learn Graphs Algorithms in Computer Science & Mathematics, theory + hands-on coding and ace Competitive Coding problems!",
                description = "Welcome to Graph Algorithms for Competitive Coding - the most detailed Specialisation in Graph Theory for Competitive Programmers, Software Engineers & Computer Science students!\n" +
                        "\n" +
                        "\n" +
                        "Graphs is quite an important topic for software engineers, both for academics & online competitions and for solving real life challenges. Graph algorithms form the very fundamentals of many popular applications like - Google Maps, social media apps like Facebook, Instagram, Quora, LinkedIn, Computer Vision applications such as image segmentation, resolving dependencies while compile time, vehicle routing problems in supply chain and many more. This course provides a detailed overview of Graph Theory algorithms in computer science, along with hands on implementation of all the algorithms in C++. Not just that you will get 80+ competitive coding questions, to practice & test your skills! \n" +
                        "\n" +
                        "This comprehensive course is taught by Prateek Narang & Apaar Kamal, who are Software Engineers at Google and have taught over thousands of students in competitive programming over last 5+ years. This course is worth thousands of dollars, but Coding Minutes is providing you this course to you at a fraction of its original cost! This is action oriented course, we not just delve into theory but focus on the practical aspects by building implementing algorithms & solving problems. With over 95+ high quality video lectures, easy to understand explanations this is one of the most detailed and robust course for Graph Algorithms ever created.\n" +
                        "\n" +
                        "Course starts very basics with how to store and represent graphs on a computer, and then dives into popular algorithms & techniques for problem solving. The course is divided into two parts.",
                price = 1499000.0,
                promotionalVideo = Video(),
                language = "ylTlDABgESXAzOHGyAxR", // English
                category = "hJqfxq5tTYVFsw69Mts9",
                thumbnail = Image("https://img-c.udemycdn.com/course/480x270/927356_8108_7.jpg", "") // replace with your dummy image
            )
            c.id = "course$i"
            courses.add(c)
        }
        return courses
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