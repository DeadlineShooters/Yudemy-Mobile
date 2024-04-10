package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.dialogs.CertificateDialog
import com.deadlineshooters.yudemy.dialogs.QADialog

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
    private lateinit var certificate: TextView

    val title = "More"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_COURSE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qa = view.findViewById(R.id.qa)
        certificate = view.findViewById(R.id.certificate)

        qa.setOnClickListener {
//            val qaDialog = QADialog(courseId!!)
//            qaDialog.show(parentFragmentManager, "QADialog")
            val qaDialog = QADialog("2tNxr8j5FosEueZrL3wH")
            qaDialog.show(parentFragmentManager, "QADialog")
        }

        certificate.setOnClickListener {
            val certificateDialog = CertificateDialog("2tNxr8j5FosEueZrL3wH")
            certificateDialog.show(parentFragmentManager, "CertificateDialog")
        }
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