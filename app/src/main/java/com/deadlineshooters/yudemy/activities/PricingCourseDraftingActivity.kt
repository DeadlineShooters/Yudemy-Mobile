package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityPricingCourseDraftingBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository

class PricingCourseDraftingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPricingCourseDraftingBinding
    private var newPrice = 0.0

    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPricingCourseDraftingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        course = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra("course", Course::class.java)!!
        else
            intent.getParcelableExtra<Course>("course")!!
        newPrice = course.price

        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.currency))
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = currencyAdapter

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.pricing_type))
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypePricing.adapter = typeAdapter
        binding.spinnerTypePricing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position == 0) {
                    binding.etPriceSetting.visibility = View.GONE
                    newPrice = 0.0
                }
                else {
                    binding.etPriceSetting.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when nothing is selected
            }
        }

        if(course.price == 0.0) {
            binding.spinnerTypePricing.setSelection(0)
            binding.etPriceSetting.visibility = View.GONE
        }
        else {
            binding.spinnerTypePricing.setSelection(1)
            binding.etPriceSetting.visibility = View.VISIBLE
            binding.etPriceSetting.setText(course.price.toString())
        }

        binding.btnSavePricing.setOnClickListener {
            if(binding.spinnerTypePricing.selectedItemPosition == 1) {
                if(binding.etPriceSetting.text.toString().isNotEmpty()) {
                    newPrice = binding.etPriceSetting.text.toString().toDouble()
                }
                else {
                    Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if(course.price != newPrice) {
                course.price = newPrice

                CourseRepository().updatePrice(course.id, newPrice)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Price updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("course", course)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
            }
            else {
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarPricingSetting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarPricingSetting.setNavigationOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}