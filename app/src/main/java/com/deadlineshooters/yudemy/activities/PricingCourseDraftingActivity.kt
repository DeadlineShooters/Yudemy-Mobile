package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityPricingCourseDraftingBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository
import com.deadlineshooters.yudemy.utils.Constants
import java.text.NumberFormat
import java.util.*

class PricingCourseDraftingActivity : BaseActivity() {
    private lateinit var binding: ActivityPricingCourseDraftingBinding
    private var newPrice  = 0

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

        val tierStrings = Constants.PRICE_TIERS.map {
            if(it == 0) "Free" else
                NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(it.toInt())
        }
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.currency))
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = currencyAdapter

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tierStrings)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypePricing.adapter = typeAdapter

        binding.spinnerTypePricing.setSelection(Constants.PRICE_TIERS.indexOf(course.price))

        binding.btnSavePricing.setOnClickListener {
            newPrice = Constants.PRICE_TIERS[binding.spinnerTypePricing.selectedItemPosition].toInt()

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
            newPrice = Constants.PRICE_TIERS[binding.spinnerTypePricing.selectedItemPosition]
            if (course.price != newPrice) {
                AlertDialog.Builder(this)
                    .setTitle("Discard changes?")
                    .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                    .setPositiveButton(Html.fromHtml("<font color='#B32D0F'><b>Discard</b></font>")) { _, _ ->
                        finish()
                    }
                    .setNegativeButton(Html.fromHtml("<font color='#5624D0'><b>Cancel</b></font>"), null)
                    .show()
            } else {
                finish()
            }
        }
    }
}