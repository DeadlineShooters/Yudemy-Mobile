package com.deadlineshooters.yudemy.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CertificateDialog(private val courseId: String) : DialogFragment() {
    private lateinit var closeBtn: TextView
    private lateinit var certificateCourseName: TextView
    private lateinit var courseInstructors: TextView
    private lateinit var studentName: TextView
    private lateinit var finishDate: TextView
    private lateinit var courseLength: TextView
    private val dumpCertificate = Certificate("123", "13/03/2024", "123", "Data Structures & Algorithms Essentials using C++ (2022)", 97200,)
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("MMM, dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_certificate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        closeBtn = view.findViewById(R.id.closeBtn)
        certificateCourseName = view.findViewById(R.id.certificateCourseName)
        courseInstructors = view.findViewById(R.id.courseInstructors)
        studentName = view.findViewById(R.id.studentName)
        finishDate = view.findViewById(R.id.finishDate)
        courseLength = view.findViewById(R.id.courseLength)

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
//            dismiss()
            Log.d("CourseId", courseId)
        }
    }
}