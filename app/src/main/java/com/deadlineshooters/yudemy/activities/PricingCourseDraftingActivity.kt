package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.deadlineshooters.yudemy.R

class PricingCourseDraftingActivity : AppCompatActivity() {
    private lateinit var toolbarPricing: Toolbar
    private lateinit var spinnerCurrency: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var price: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pricing_course_drafting)

        toolbarPricing = findViewById(R.id.toolbar_pricing_setting)
        setupActionBar()

        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        spinnerType = findViewById(R.id.spinnerTypePricing)
        price = findViewById(R.id.etPriceSetting)

        val currenncyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.currency))
        currenncyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = currenncyAdapter

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.pricing_type))
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position == 0) {
                    price.visibility = View.GONE
                }
                else {
                    price.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when nothing is selected
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbarPricing)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbarPricing.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}