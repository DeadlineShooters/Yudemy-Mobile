package com.deadlineshooters.yudemy.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CertificateActivity : AppCompatActivity() {
    private lateinit var closeBtn: Button
    private lateinit var certificateCourseName: TextView
    private lateinit var courseInstructors: TextView
    private lateinit var studentName: TextView
    private lateinit var finishDate: TextView
    private lateinit var courseLength: TextView
    private val dumpCertificate = Certificate("123", "13/03/2024", "123", "Data Structures & Algorithms Essentials using C++ (2022)", 97200,)
    val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val newFormat = SimpleDateFormat("MMM, dd, yyyy", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate)

        closeBtn = findViewById(R.id.closeBtn)
        certificateCourseName = findViewById(R.id.certificateCourseName)
        courseInstructors = findViewById(R.id.courseInstructors)
        studentName = findViewById(R.id.studentName)
        finishDate = findViewById(R.id.finishDate)
        courseLength = findViewById(R.id.courseLength)

        certificateCourseName.text = dumpCertificate.title
        courseInstructors.text = "Prateek Narang"
        studentName.text = "Phước Tài"
        val date: Date = originalFormat.parse(dumpCertificate.createdDate) ?: Date()
        val formattedDate: String = newFormat.format(date)
        finishDate.text = formattedDate
        val hours = dumpCertificate.length / 3600
        val minutes = (dumpCertificate.length % 3600) / 60
        val seconds = dumpCertificate.length % 60

        val formattedLength = when {
            hours == 0 -> "Total $minutes minutes $seconds seconds"
            minutes == 0 -> "Total $hours hours $seconds seconds"
            seconds == 0 -> "Total $hours hours $minutes minutes"
            else -> "Total $hours hours $minutes minutes $seconds seconds"
        }

        courseLength.text = formattedLength

        closeBtn.setOnClickListener {
            //TODO: Close the activity
        }
    }
}