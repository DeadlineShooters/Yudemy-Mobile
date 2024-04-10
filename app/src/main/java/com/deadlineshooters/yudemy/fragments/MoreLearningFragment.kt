package com.deadlineshooters.yudemy.fragments

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.databinding.FragmentMoreLearningBinding
import com.deadlineshooters.yudemy.dialogs.CertificateDialog
import com.deadlineshooters.yudemy.dialogs.QADialog
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.viewmodels.CertificateViewModel
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURSE = "course"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreLearningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreLearningFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var course: Course? = null
    private lateinit var qa: TextView
    private lateinit var binding: FragmentMoreLearningBinding
    private lateinit var aboutDialog: BottomSheetDialog
    private lateinit var certificate: TextView
    private lateinit var certificateViewModel: CertificateViewModel

    val title = "More"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arguments?.let {
                course = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable(ARG_COURSE, Course::class.java)
                } else {
                    it.getParcelable(ARG_COURSE)
                }
            }
        }
        certificateViewModel = ViewModelProvider(this)[CertificateViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreLearningBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qa = view.findViewById(R.id.qa)
        certificate = view.findViewById(R.id.certificate)
        qa.setOnClickListener {
//            val qaDialog = QADialog(courseId!!)
//            qaDialog.show(parentFragmentManager, "QADialog")
            val qaDialog = QADialog(course!!.id)
            qaDialog.show(parentFragmentManager, "QADialog")
        }

        certificate.setOnClickListener {
            certificateViewModel.getCertificate(BaseActivity().getCurrentUserID() ,course!!.id)
            certificateViewModel.certificate.observe(viewLifecycleOwner, Observer {
                if(it != null) {
                    val certificateDialog = CertificateDialog(course!!.id)
                    certificateDialog.show(parentFragmentManager, "CertificateDialog")
                } else {
                    BaseActivity().showNoButtonDialogWithTimeout(requireContext(), "Certificate", "You have not completed this course yet", 2000)
                }
            })
        }

        aboutDialog = createAboutDialog()
        binding.navAbout.setOnClickListener {
            aboutDialog.show()

            aboutDialog.findViewById<TextView>(R.id.contentAboutCourse)!!.text = course!!.description
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
        fun newInstance(course: Course) =
            MoreLearningFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_COURSE, course)
                }
            }
    }
}