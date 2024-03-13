package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.FragmentAnalyticsBinding
import com.deadlineshooters.yudemy.databinding.FragmentFeedbackBinding


class AnalyticsFragment : Fragment() {
    private lateinit var binding: FragmentAnalyticsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get a reference to the Spinner

// Create an array of options
        val dateOptions = arrayOf("Last 7 days", "Last 30 days", "Last 12 months", "All time")

// Create an ArrayAdapter using the array and a default spinner layout
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dateOptions
        )

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the Spinner
        binding.dateRangeFilter.adapter = adapter

    }

}