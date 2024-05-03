package com.deadlineshooters.yudemy.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityCourseDetailBinding
import com.deadlineshooters.yudemy.databinding.ActivityCourseRevenueAnalyticsBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.TransactionViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class CourseRevenueAnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseRevenueAnalyticsBinding
    private lateinit var transactionViewModel : TransactionViewModel
    private lateinit var course : Course
    private var transactions: Map<String, Int> = mapOf()
    var noDataMessageShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseRevenueAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val dateOptions = arrayOf("Last 7 days", "Last 30 days", "Last 12 months", "All time")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            dateOptions
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.courseDateRangeFilter.adapter = adapter

        transactionViewModel.groupedTransactions.observe(this, Observer { transactions ->
            this.transactions = transactions
            updateChart(filterTransactionsBasedOnRange("Last 7 days", transactions), "Last 7 days")
        })

        course = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra("course", Course::class.java)!!
        else
            intent.getParcelableExtra<Course>("course")!!

        binding.tvCourseTitle.text = course.name


        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.tvCourseTotalRevenue.text = currencyFormat.format(course.totalRevenue)

        // Get transactions for the current user and a specific course
        transactionViewModel.getTransactionsForCourseGroupedByDate(UserRepository.getCurrentUserID(), courseId = course.id)

        binding.courseDateRangeFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedRange = parent.getItemAtPosition(position).toString()

                // Update your data based on the selected range
                val filteredTransactions = filterTransactionsBasedOnRange(selectedRange, transactions)

                // Update the chart with the filtered data
                updateChart(filteredTransactions, selectedRange) // Pass the selectedRange as the second argument
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing or handle 'nothing selected' state if necessary
            }
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarRevenue)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }

        binding.toolbarRevenue.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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