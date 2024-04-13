package com.deadlineshooters.yudemy.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.adapters.BottomSheetDialogAdapter
import com.deadlineshooters.yudemy.adapters.QuestionListAdapter
import com.deadlineshooters.yudemy.adapters.ReplyListAdapter
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
import com.deadlineshooters.yudemy.viewmodels.LectureViewModel
import com.deadlineshooters.yudemy.viewmodels.QuestionViewModel
import com.deadlineshooters.yudemy.viewmodels.ReplyViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InstructorQAFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InstructorQAFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var qaInstructorFilterBtn: Button
    private lateinit var instructorQuestionListView: RecyclerView
    private lateinit var instructorFilterQuestionDialog: BottomSheetDialog
    private lateinit var startForImagePickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var questionDetailDialog: Dialog
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())
    private var imageList: ArrayList<String> = ArrayList()

    private lateinit var questionListAdapter: QuestionListAdapter
    private lateinit var questionViewModel: QuestionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        questionViewModel = QuestionViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instructor_q_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qaInstructorFilterBtn = view.findViewById(R.id.qaInstructorFilterBtn)
        instructorQuestionListView = view.findViewById(R.id.instructorQuestionListView)
        questionViewModel.getQuestionsOfInstructor(BaseActivity().getCurrentUserID())

        questionViewModel.questions.observe(viewLifecycleOwner, Observer{ result ->
            questionListAdapter = QuestionListAdapter(result, this, questionViewModel)
            instructorQuestionListView.adapter = questionListAdapter
            instructorQuestionListView.layoutManager = LinearLayoutManager(requireContext())

            questionListAdapter.onItemClick = { question ->
                // TODO: check if the clicked question has asker = user._id
                questionViewModel.getQuestionById(question._id)
                questionDetailDialog = createQuestionDetailDialog()
                questionDetailDialog.show()
            }
        })

        qaInstructorFilterBtn.setOnClickListener {
            instructorFilterQuestionDialog = createInstructorQuestionFilterDialog()
            instructorFilterQuestionDialog.show()
        }

        startForImagePickerResult = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                uris.forEach { uri ->
                    addImageView(uri)
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun createInstructorQuestionFilterDialog(): BottomSheetDialog { //, R.style.MyTransparentBottomSheetDialogTheme
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        val adapter = BottomSheetDialogAdapter(resources.getStringArray(R.array.instructor_questions_filter).toCollection(ArrayList()))
        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter, filterIdx ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
            questionListAdapter.instructorFilterQuestion(filter)
            dialog.dismiss()
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }

    private fun addImageView(uri: Uri) {
        val imageContainer = questionDetailDialog.findViewById(R.id.repImageContainer) as LinearLayout

        val imageView = ImageView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            200,
            200,
        )
        layoutParams.setMargins(0, 0, 16, 0)
        imageView.layoutParams = layoutParams
        imageView.setImageURI(uri)

        imageView.tag = uri.toString()
        imageView.setOnClickListener {
            imageContainer.removeView(imageView)
        }

        imageContainer.addView(imageView)
        imageView.drawable?.let{drawble ->
            val bitmap = (drawble as BitmapDrawable).bitmap
            val filepath = BaseActivity().saveBitmapToFile(bitmap, requireContext())
            imageList.add(filepath.toString())
        }
    }

    private fun createQuestionDetailDialog(): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_question_detail, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)

        val backQuestionDetailBtn = sheet.findViewById<TextView>(R.id.backQuestionDetailBtn)
        val questionDetailTitle = sheet.findViewById<TextView>(R.id.questionDetailTitle)
        val questionDetailAskerName = sheet.findViewById<TextView>(R.id.questionDetailAskerName)
        val questionDetailAskDate = sheet.findViewById<TextView>(R.id.questionDetailAskDate)
        val questionDetailLectureId = sheet.findViewById<TextView>(R.id.questionDetailLectureId)
        val questionDetailContentView = sheet.findViewById<ConstraintLayout>(R.id.questionDetailContentView)
        val questionDetailContent = sheet.findViewById<TextView>(R.id.questionDetailContent)

        val replyListView = sheet.findViewById<RecyclerView>(R.id.replyListView)
        val cameraBtn1 = sheet.findViewById<Button>(R.id.cameraBtn1)
        val sendBtn = sheet.findViewById<Button>(R.id.sendBtn)
        val deleteQuestionBtn = sheet.findViewById<Button>(R.id.deleteQuestionBtn)
        val editQuestionBtn = sheet.findViewById<TextView>(R.id.editQuestionBtn)
        val questionDetailImageContainer = sheet.findViewById<LinearLayout>(R.id.questionDetailImageContainer)
        val repImageContainer = sheet.findViewById<LinearLayout>(R.id.repImageContainer)
        val replyContent = sheet.findViewById<TextView>(R.id.replyInput)
        val askerImage = sheet.findViewById<ImageView>(R.id.questionDetailAskerImage)

        lateinit var replyListAdapter: ReplyListAdapter
        val userViewModel: UserViewModel = UserViewModel()
        val lectureViewModel: LectureViewModel = LectureViewModel()
        val replyViewModel: ReplyViewModel = ReplyViewModel()



        questionViewModel.question.observe(viewLifecycleOwner, Observer { question ->
            userViewModel.getUserById(question.asker)
            lectureViewModel.getLectureById(question.lectureId)
            replyViewModel.getRepliesByQuestionId(question._id)

            questionDetailTitle.text = question.title
            userViewModel.userData.observe(viewLifecycleOwner, Observer { it ->
                questionDetailAskerName.text = it.fullName
                ImageViewHelper().setImageViewFromUrl(it.image, askerImage)
            })
            val date: Date = originalFormat.parse(question.createdTime) ?: Date()
            val formattedDate: String = newFormat.format(date)
            questionDetailAskDate.text = formattedDate

            lectureViewModel.lecture.observe(viewLifecycleOwner, Observer { it ->
                questionDetailLectureId.text = it.name
            })

            replyViewModel.replies.observe(viewLifecycleOwner, Observer { it ->
                replyListAdapter = ReplyListAdapter(it, this)
                replyListView.adapter = replyListAdapter
                replyListView.layoutManager = LinearLayoutManager(requireContext())
            })
            questionDetailContent.text = question.details

            if(question.images.isNotEmpty()){
                questionDetailImageContainer.removeAllViews()
                for (imageUrl in question.images) {
                    val imageView = ImageView(questionDetailContentView.context)
                    imageView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        350
                    )

                    imageView.adjustViewBounds = true
                    imageView.setPadding(0, 0, 16, 16)
                    ImageViewHelper().setImageViewFromUrl(imageUrl, imageView)
                    questionDetailImageContainer.addView(imageView)
                }
            }

            sendBtn.setOnClickListener{
                //TODO: Submit reply to database
                val reply = replyContent.text.toString()
                if(imageList.isNotEmpty()){
                    CloudinaryHelper().uploadImageListToCloudinary(imageList){
                        val rep = Reply("", BaseActivity().getCurrentUserID(), question._id , it, reply , originalFormat.format(Date()))
                        replyViewModel.addNewReply(rep)
                        imageList.clear()
                        replyContent.text = ""
                        repImageContainer.removeAllViews()
                        replyListAdapter.notifyItemInserted(replyListAdapter.itemCount)
                        questionListAdapter.notifyDataSetChanged()
                        imageList.clear()
                    }
                } else{
                    val rep = Reply("", BaseActivity().getCurrentUserID(), question._id , ArrayList(), reply , originalFormat.format(Date()))
                    replyViewModel.addNewReply(rep)
                    replyContent.text = ""
                    replyListAdapter.notifyItemInserted(replyListAdapter.itemCount)
                    questionListAdapter.notifyDataSetChanged()
                }
            }

        })

        backQuestionDetailBtn.setOnClickListener{
            imageList.clear()
            dialog.dismiss()
        }

        cameraBtn1.setOnClickListener {
            startForImagePickerResult.launch(PickVisualMediaRequest())
        }

        deleteQuestionBtn.visibility = View.GONE
        editQuestionBtn.visibility = View.GONE

        dialog.setContentView(sheet)
        return dialog
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InstructorQAFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InstructorQAFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}