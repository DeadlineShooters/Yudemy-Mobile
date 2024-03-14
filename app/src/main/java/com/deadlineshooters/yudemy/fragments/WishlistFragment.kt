package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.CategoryAdapter2
import com.deadlineshooters.yudemy.adapters.CourseListAdapter1
import com.deadlineshooters.yudemy.databinding.FragmentWishlistBinding
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WishlistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WishlistFragment : Fragment() {
    private lateinit var courseViewModel: CourseViewModel
    private var _binding: FragmentWishlistBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories = listOf(
            "Development", "Business", "Office Productivity", "Design",
            "Marketing", "Photography & Video", "Teaching & Academics",
            "Finance & Accounting", "IT & Software", "Personal Development",
            "Lifestyle", "Health & Fitness", "Music"
        )

        val adapter = CategoryAdapter2(categories)
        binding.categoryList.adapter = adapter
        binding.categoryList.layoutManager = LinearLayoutManager(context)
        binding.categoryList.addItemDecoration(FeaturedFragment.SpaceItemDecoration(8))

        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        courseViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            val clonedCourses = List(10) { courses[0] }
            val wishListAdapter = CourseListAdapter1(requireContext(), R.layout.course_list_item, clonedCourses)
            binding.wishlistList.adapter = wishListAdapter
            if (courses.isEmpty()) {
                binding.emptyFrame.visibility = View.VISIBLE
                binding.wishlistList.visibility = View.GONE
            } else {
                binding.emptyFrame.visibility = View.GONE
                binding.wishlistList.visibility = View.VISIBLE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WishlistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = WishlistFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}