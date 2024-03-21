package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.adapters.CheckboxAdapter
import com.deadlineshooters.yudemy.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sortOption = listOf("Ratings", "Newest")
        ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOption)
            .also { adapter ->
                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                binding.spinner.adapter = adapter
            }

        val priceOption = listOf("Paid", "Free")
        val priceAdapter = CheckboxAdapter(priceOption) { item, isChecked ->
            if (isChecked) {
                // Code to execute when an item is selected
            } else {
                // Code to execute when an item is deselected
            }
        }
        binding.priceList.layoutManager = LinearLayoutManager(this)
        binding.priceList.adapter = priceAdapter

        val featureOption = listOf("Subtitles", "Quizzes", "Coding Exercises")
        val featureAdapter = CheckboxAdapter(featureOption) { item, isChecked ->
            if (isChecked) {
                // Code to execute when an item is selected
            } else {
                // Code to execute when an item is deselected
            }
        }
        binding.featureList.layoutManager = LinearLayoutManager(this)
        binding.featureList.adapter = featureAdapter


        val languageOption = listOf("English", "Vietnamese")
        val languageAdapter = CheckboxAdapter(languageOption) { item, isChecked ->
            if (isChecked) {
                // Code to execute when an item is selected
            } else {
                // Code to execute when an item is deselected
            }
        }
        binding.languageList.layoutManager = LinearLayoutManager(this)
        binding.languageList.adapter = languageAdapter

        val ratingOption = listOf("4.5 & up", "4.0 & up", "3.5 & up", "3.0 & up")
        val ratingAdapter = CheckboxAdapter(ratingOption) { item, isChecked ->
            if (isChecked) {
                // Code to execute when an item is selected
            } else {
                // Code to execute when an item is deselected
            }
        }
        binding.ratingList.layoutManager = LinearLayoutManager(this)
        binding.ratingList.adapter = ratingAdapter

        val durationOption = listOf("0-1 Hours", "1-3 Hours", "3-6 Hours", "6+ Hours")
        val durationAdapter = CheckboxAdapter(durationOption) { item, isChecked ->
            if (isChecked) {
                // Code to execute when an item is selected
            } else {
                // Code to execute when an item is deselected
            }
        }
        binding.durationList.layoutManager = LinearLayoutManager(this)
        binding.durationList.adapter = durationAdapter
    }
}