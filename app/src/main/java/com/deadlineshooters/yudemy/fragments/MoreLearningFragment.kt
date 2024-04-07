package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.FragmentAnalyticsBinding
import com.deadlineshooters.yudemy.databinding.FragmentMoreLearningBinding
import com.deadlineshooters.yudemy.dialogs.CertificateDialog
import com.deadlineshooters.yudemy.dialogs.QADialog
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURSE_ID = "courseId"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreLearningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreLearningFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var courseId: String? = null
    private lateinit var qa: TextView
    private lateinit var binding: FragmentMoreLearningBinding
    private lateinit var aboutDialog: BottomSheetDialog
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var certificate: TextView

    val title = "More"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            courseId = it.getString(ARG_COURSE_ID) TODO: Uncomment this line
            courseId = "2tNxr8j5FosEueZrL3wH"
        }

        courseViewModel = ViewModelProvider(requireActivity())[CourseViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMoreLearningBinding.inflate(inflater, container, false)

        binding.tvLeaveRating.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.course_feedback_popup_layout)


            val closeButton = dialog.findViewById<ImageView>(R.id.iv_exit)
            closeButton.setOnClickListener {
                // Handle close button click here
                dialog.dismiss()
            }

            dialog.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qa = view.findViewById(R.id.qa)
        certificate = view.findViewById(R.id.certificate)
        qa.setOnClickListener {
            val qaDialog = QADialog()
            qaDialog.show(parentFragmentManager, "QADialog")
        }
        certificate.setOnClickListener {
            val certificateDialog = CertificateDialog()
            certificateDialog.show(parentFragmentManager, "CertificateDialog")
        }

        aboutDialog = createAboutDialog()
        binding.navAbout.setOnClickListener {
            aboutDialog.show()

            courseViewModel.learningCourse.observe(viewLifecycleOwner, Observer{
                aboutDialog.findViewById<TextView>(R.id.contentAboutCourse)!!.text = it?.description
            })
        }
    }

    private fun createAboutDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.StandardBottomSheetDialog)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_about_this_course, null)

        bottomSheet.findViewById<TextView>(R.id.closeDialogAbout).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val layout = dialog.findViewById<CoordinatorLayout>(R.id.aboutBottomSheetLayout)
        if (layout != null) {
            layout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }

        return dialog
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param courseId Course ID.
         * @return A new instance of fragment MoreLearningFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(courseId: String?) =
            MoreLearningFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, courseId)
                }
            }
    }
}