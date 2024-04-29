package com.deadlineshooters.yudemy.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.DropBoxManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.databinding.FragmentAnalyticsBinding
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.TransactionViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class AnalyticsFragment : Fragment() {
    private lateinit var binding: FragmentAnalyticsBinding
    private lateinit var transactionViewModel : TransactionViewModel
    private lateinit var courseViewModel: CourseViewModel
    private var currentUser : String = UserRepository.getCurrentUserID()
    private var transactions: Map<String, Int> = mapOf()
    var noDataMessageShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)

        transactionViewModel = ViewModelProvider(requireActivity()).get(TransactionViewModel::class.java)
        courseViewModel = ViewModelProvider(requireActivity()).get(CourseViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateOptions = arrayOf("Last 7 days", "Last 30 days", "Last 12 months", "All time")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dateOptions
        )

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the Spinner
        binding.dateRangeFilter.adapter = adapter

        binding.chart.description.isEnabled = false



        transactionViewModel.groupedTransactions.observe(viewLifecycleOwner, Observer { transactions ->
            this.transactions = transactions
            updateChart(filterTransactionsBasedOnRange("Last 7 days", transactions), "Last 7 days")
        })



        // Get transactions for the current user
        transactionViewModel.getTransactionsGroupedByDate(currentUser)

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        courseViewModel.getTotalRevenueForInstructor(currentUser).addOnSuccessListener { totalRevenue ->
            binding.tvTotalRevenue.text = currencyFormat.format(totalRevenue)
        }

        binding.dateRangeFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedRange = parent.getItemAtPosition(position).toString()

                // Update your data based on the selected range
                // For example, if the selected range is "Last 7 days", filter your data to include only the last 7 days
                val filteredTransactions = filterTransactionsBasedOnRange(selectedRange, transactions)

                // Update the chart with the filtered data
                updateChart(filteredTransactions, selectedRange) // Pass the selectedRange as the second argument
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing or handle 'nothing selected' state if necessary
            }
        }


    }

    fun updateChart(transactions: Map<String, Int>, range: String) {
        if (transactions.isEmpty()) {
            binding.chart.clear() // Clear the chart
            if (!noDataMessageShown) {
                Toast.makeText(this, "No data to display", Toast.LENGTH_SHORT).show() // Display a message
                noDataMessageShown = true
            }
            return
        }

        var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val entries = transactions.map {
            Entry(sdf.parse(it.key + " 00:00:00").time.toFloat(), it.value.toFloat())
        }.sortedBy { it.x }

        val lineDataSet = LineDataSet(entries, "Revenue")
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = Color.BLACK

        val lineData = LineData(lineDataSet)
        binding.chart.data = lineData

        // Create a formatter that converts the x-values (timestamps) back to dates
        sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val min = binding.chart.xAxis.axisMinimum
                val max = binding.chart.xAxis.axisMaximum
                val mid = (min + max) / 2

                return when {
                    value <= min -> sdf.format(Date(value.toLong() * TimeUnit.DAYS.toMillis(1))) // Min date
                    value >= max -> sdf.format(Date(value.toLong() * TimeUnit.DAYS.toMillis(1))) // Max date
                    abs(value - mid) < 1 -> sdf.format(Date(value.toLong() * TimeUnit.DAYS.toMillis(1))) // Middle date
                    else -> "" // Don't show other labels
                }
            }
        }

        // Set the formatter to the x-axis
        binding.chart.xAxis.valueFormatter = formatter

        // Set the minimum and maximum values of the x-axis based on the range
        val now = System.currentTimeMillis().toFloat()
        binding.chart.xAxis.axisMinimum = when (range) {
            "Last 7 days" -> now - TimeUnit.DAYS.toMillis(6).toFloat() // Start date 7 days ago
            "Last 30 days" -> now - TimeUnit.DAYS.toMillis(29).toFloat() // Start date 30 days ago
            "Last 12 months" -> now - TimeUnit.DAYS.toMillis(365).toFloat() // Start date 12 months ago
            else -> binding.chart.xAxis.axisMinimum
        }
        binding.chart.xAxis.axisMaximum = now

        binding.chart.invalidate() // refresh the chart
    }


    fun filterTransactionsBasedOnRange(range: String, transactions: Map<String, Int>): Map<String, Int> {
        if (transactions.isEmpty()) {
            return mapOf()
        }

        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val filteredTransactions = when (range) {
            "Last 7 days" -> {
                val startDate = now - TimeUnit.DAYS.toMillis(6) // Start date 7 days ago
                transactions.filter { sdf.parse(it.key + " 00:00:00").time in startDate..now }
            }
            "Last 30 days" -> {
                val startDate = now - TimeUnit.DAYS.toMillis(29) // Start date 30 days ago
                transactions.filter { sdf.parse(it.key + " 00:00:00").time in startDate..now }
            }
            "Last 12 months" -> {
                val startDate = now - TimeUnit.DAYS.toMillis(365) // Start date 12 months ago
                transactions.filter { sdf.parse(it.key + " 00:00:00").time in startDate..now }
            }
            "All time" -> transactions
            else -> transactions
        }

        return filteredTransactions
    }


}