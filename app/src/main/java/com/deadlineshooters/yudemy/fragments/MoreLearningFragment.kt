package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.activities.CourseLearningActivity
import com.deadlineshooters.yudemy.databinding.FragmentMoreLearningBinding
import com.deadlineshooters.yudemy.dialogs.CertificateDialog
import com.deadlineshooters.yudemy.dialogs.QADialog
import com.deadlineshooters.yudemy.helpers.DialogHelper
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository
import com.deadlineshooters.yudemy.repositories.FeedbackCallback
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CertificateViewModel
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
    private var courseFeedbackRepo = CourseFeedbackRepository()
    private var userRepository = UserRepository()
    private lateinit var dialog: Dialog
    private lateinit var ratingBar: RatingBar
    private lateinit var feedbackEditText: EditText
    private lateinit var certificateViewModel: CertificateViewModel

    val title = "More"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arguments?.let {
                course = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable(ARG_COURSE, Course::class.java)
                } else {
                    it.getParcelable(ARG_COURSE)
                }
            }
        }
        certificateViewModel = ViewModelProvider(this)[CertificateViewModel::class.java]
    }

    private fun setupDialog() {
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.course_feedback_popup_layout)

        ratingBar = dialog.findViewById(R.id.ratingBar)
        feedbackEditText = dialog.findViewById(R.id.feedback)
    }

    private fun updateUIWithFeedback(feedback: CourseFeedback?) {
        // Update the UI with the new feedback
        ratingBar.rating = feedback?.rating?.toFloat() ?: 0f
        feedbackEditText.setText(feedback?.feedback ?: "")
        binding.tvLeaveRating.text = if (feedback != null) "Edit rating" else "Leave a rating"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMoreLearningBinding.inflate(inflater, container, false)

        setupDialog()

        // Get feedback for course and user
        DialogHelper.showProgressDialog(requireContext(), "Loading...")
        courseFeedbackRepo.getFeedbackForCourseAndUser(
            course!!.id,
            UserRepository.getCurrentUserID()
        ) { feedback ->
            DialogHelper.hideProgressDialog()
            updateUIWithFeedback(feedback)
        }
        binding.tvLeaveRating.setOnClickListener {
            courseFeedbackRepo.getFeedbackForCourseAndUser(
                course!!.id,
                UserRepository.getCurrentUserID()
            ) { feedback ->
                updateUIWithFeedback(feedback)

                // If there is feedback, set the rating and feedback text
                if (feedback != null) {
                    ratingBar.rating = feedback.rating.toFloat()
                    feedbackEditText.setText(feedback.feedback)
                }

                val closeButton = dialog.findViewById<ImageView>(R.id.iv_exit)
                closeButton.setOnClickListener {
                    // Handle close button click here
                    dialog.dismiss()
                }
                dialog.setCanceledOnTouchOutside(false)

                val saveButton = dialog.findViewById<Button>(R.id.saveButton)
                saveButton.setOnClickListener {
                    // Get the new rating and feedback text
                    val newRating = CourseFeedback(
                        courseId = course!!.id,
                        userId = UserRepository.getCurrentUserID(),
                        feedback = feedbackEditText.text.toString(),
                        rating = ratingBar.rating.toInt(),
                    )

                    // Save feedback
                    courseFeedbackRepo.saveFeedback(feedback, course!!, newRating, object :
                        FeedbackCallback {
                        override fun onSuccess() {
                            Toast.makeText(
                                context,
                                "Feedback saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Reload the fragment
                            updateUIWithFeedback(newRating)
                        }

                        override fun onFailure(e: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to save feedback: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })

                    dialog.dismiss()
                }

                dialog.show()
            }
        }

        userRepository.isInFavorites(course!!.id) { isInFavorites ->
            if (isInFavorites) {
                binding.addToFav.text = "Remove course from favorites"
            } else {
                binding.addToFav.text = "Add course to favorites"
            }
        }

        binding.addToFav.setOnClickListener {
            userRepository.isInFavorites(course!!.id) { isInFavorites ->
                if (!isInFavorites) {
                    userRepository.addToFavorites(course!!.id) {}
                    binding.addToFav.text = "Remove course from favorites"
                } else {
                    userRepository.removeFromFavorites(course!!.id) {}
                    binding.addToFav.text = "Add course to favorites"
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qa = view.findViewById(R.id.qa)
        certificate = view.findViewById(R.id.certificate)
        qa.setOnClickListener {
//            val qaDialog = QADialog(courseId!!)
//            qaDialog.show(parentFragmentManager, "QADialog")

            val qaDialog = QADialog(course!!.id, (activity as CourseLearningActivity).currentLecture!!.keys.first()._id)
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

            aboutDialog.findViewById<TextView>(R.id.contentAboutCourse)!!.text =
                course!!.description
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