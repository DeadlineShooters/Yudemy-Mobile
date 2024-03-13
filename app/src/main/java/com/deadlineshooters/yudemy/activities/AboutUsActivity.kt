package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.deadlineshooters.yudemy.R

class AboutUsActivity : AppCompatActivity() {
    private lateinit var back: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        back = findViewById(R.id.backFromAboutUs)

        back.setOnClickListener {
            finish()
        }
    }
}