package com.deadlineshooters.yudemy.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.adapters.CourseListAdapter1
import com.deadlineshooters.yudemy.adapters.QuestionListAdapter
import com.deadlineshooters.yudemy.adapters.ReplyListAdapter
import com.deadlineshooters.yudemy.models.Certificate
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.CertificateRepository
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CertificateViewModel
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class CertificateDialog(private val courseId: String) : DialogFragment() {
    private lateinit var closeBtn: Button
    private lateinit var certificateCourseName: TextView
    private lateinit var courseInstructors: TextView
    private lateinit var studentName: TextView
    private lateinit var finishDate: TextView
    private lateinit var courseLength: TextView
//    private val dumpCertificate = Certificate("123", "13/03/2024", BaseActivity().getCurrentUserID(),"123", "Data Structures & Algorithms Essentials using C++ (2022)", 97200,)
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("MMM, dd, yyyy", Locale.getDefault())
    private lateinit var certificateViewModel: CertificateViewModel
    private lateinit var instructorViewModel: InstructorViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        certificateViewModel = ViewModelProvider(this)[CertificateViewModel::class.java]
        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        certificateViewModel.getCertificate(BaseActivity().getCurrentUserID(), courseId)
        instructorViewModel.getInstructorByCourse(courseId)
        userViewModel.getCurUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_certificate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeBtn = view.findViewById(R.id.closeBtn)
        certificateCourseName = view.findViewById(R.id.certificateCourseName)
        courseInstructors = view.findViewById(R.id.courseInstructors)
        studentName = view.findViewById(R.id.studentName)
        finishDate = view.findViewById(R.id.finishDate)
        courseLength = view.findViewById(R.id.courseLength)

        certificateViewModel.certificate.observe(viewLifecycleOwner, Observer {certificate ->
            certificateCourseName.text = certificate.title

            instructorViewModel.instructor.observe(viewLifecycleOwner, Observer {instructor ->
                courseInstructors.text = instructor.fullName
            })

            userViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
                studentName.text = user.fullName
            })

//            studentName.text = UserRepository().getCurUser().fullName
//            Log.d("Certificate", "Cur user: ${UserRepository().getCurUser()}")
            val date: Date = originalFormat.parse(certificate.createdDate) ?: Date()
            val formattedDate: String = newFormat.format(date)
            finishDate.text = formattedDate
            val hours = (certificate.length / 3600)
            val minutes = (certificate.length % 3600) / 60
            val seconds = certificate.length % 60

            Log.d("Certificate", "Hours: $hours, Minutes: $minutes, Seconds: $seconds")

            val formattedLength = when {
                hours == 0 -> "Total $minutes minutes $seconds seconds"
                minutes == 0 -> "Total $hours hours $seconds seconds"
                seconds == 0 -> "Total $hours hours $minutes minutes"
                else -> "Total $hours hours $minutes minutes $seconds seconds"
            }

            Log.d("Certificate", "Formatted Length: $formattedLength")

            courseLength.text = formattedLength
        })

        closeBtn.setOnClickListener {
            dismiss()
        }

    }
}