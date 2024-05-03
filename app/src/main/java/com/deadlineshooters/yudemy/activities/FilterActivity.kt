package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.adapters.CheckboxAdapter
import com.deadlineshooters.yudemy.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    val priceOption = listOf("Paid", "Free")
    val priceAdapter = CheckboxAdapter(priceOption)

    val languageOption = listOf("English", "Vietnamese")
    val languageAdapter = CheckboxAdapter(languageOption)

    val ratingOption = listOf("4.5 & up", "4.0 & up", "3.5 & up", "3.0 & up")
    val ratingAdapter = CheckboxAdapter(ratingOption)

    val durationOption = listOf("0-1 Hours", "1-3 Hours", "3-6 Hours", "6+ Hours")
    val durationAdapter = CheckboxAdapter(durationOption)

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


        binding.priceList.layoutManager = LinearLayoutManager(this)
        binding.priceList.adapter = priceAdapter

        binding.languageList.layoutManager = LinearLayoutManager(this)
        binding.languageList.adapter = languageAdapter

        binding.ratingList.layoutManager = LinearLayoutManager(this)
        binding.ratingList.adapter = ratingAdapter

        binding.durationList.layoutManager = LinearLayoutManager(this)
        binding.durationList.adapter = durationAdapter

        binding.applyBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("sortOption", binding.spinner.selectedItem.toString())
            intent.putExtra("priceOptions", priceAdapter.getCheckedItems().toTypedArray())
            intent.putExtra("languageOptions", languageAdapter.getCheckedItems().toTypedArray())
            intent.putExtra("ratingOptions", ratingAdapter.getCheckedItems().toTypedArray())
            intent.putExtra("durationOptions", durationAdapter.getCheckedItems().toTypedArray())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("checkboxState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("sortOption", binding.spinner.selectedItemPosition)
        editor.putStringSet("priceOptions", priceAdapter.selectedPositions.map { priceAdapter.items[it] }.toSet())
        editor.putStringSet("languageOptions", languageAdapter.selectedPositions.map { languageAdapter.items[it] }.toSet())
        editor.putStringSet("ratingOptions", ratingAdapter.selectedPositions.map { ratingAdapter.items[it] }.toSet())
        editor.putStringSet("durationOptions", durationAdapter.selectedPositions.map { durationAdapter.items[it] }.toSet())
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("checkboxState", Context.MODE_PRIVATE)
        binding.spinner.setSelection(sharedPreferences.getInt("sortOption", 0))
        priceAdapter.selectedPositions = sharedPreferences.getStringSet("priceOptions", emptySet())?.map { priceAdapter.items.indexOf(it) }?.toMutableSet() ?: mutableSetOf()
        languageAdapter.selectedPositions = sharedPreferences.getStringSet("languageOptions", emptySet())?.map { languageAdapter.items.indexOf(it) }?.toMutableSet() ?: mutableSetOf()
        ratingAdapter.selectedPositions = sharedPreferences.getStringSet("ratingOptions", emptySet())?.map { ratingAdapter.items.indexOf(it) }?.toMutableSet() ?: mutableSetOf()
        durationAdapter.selectedPositions = sharedPreferences.getStringSet("durationOptions", emptySet())?.map { durationAdapter.items.indexOf(it) }?.toMutableSet() ?: mutableSetOf()
    }

}