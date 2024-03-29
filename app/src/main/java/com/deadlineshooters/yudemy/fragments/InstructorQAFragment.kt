package com.deadlineshooters.yudemy.fragments

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.MyLearningFilterAdapter
import com.deadlineshooters.yudemy.adapters.QuestionListAdapter
import com.deadlineshooters.yudemy.adapters.ReplyListAdapter
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.checkerframework.common.subtyping.qual.Bottom
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

    private val dumpQuestion1 = Question("123", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion2 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion3 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion4 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion5 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion6 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion7 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion8 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion9 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")



    private val dumpQuestionList = arrayListOf(dumpQuestion1, dumpQuestion2, dumpQuestion3, dumpQuestion4, dumpQuestion5, dumpQuestion6, dumpQuestion7, dumpQuestion8, dumpQuestion9)

    private var questionListAdapter = QuestionListAdapter(dumpQuestionList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

        qaInstructorFilterBtn.setOnClickListener {
            instructorFilterQuestionDialog = createInstructorQuestionFilterDialog()
            instructorFilterQuestionDialog.show()
        }

        questionListAdapter = QuestionListAdapter(dumpQuestionList)
        instructorQuestionListView.adapter = questionListAdapter
        instructorQuestionListView.layoutManager = LinearLayoutManager(requireContext())

        questionListAdapter.onItemClick = { question ->
            // TODO: check if the clicked question has asker = user._id
            questionDetailDialog = createQuestionDetailDialog(question)
            questionDetailDialog.show()
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
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_my_learning_filter, null)
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        val adapter = MyLearningFilterAdapter(resources.getStringArray(R.array.instructor_questions_filter))
        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }

    private fun addImageView(uri: Uri) {
        val imageContainer = questionDetailDialog.findViewById(R.id.replyImageContainer) as LinearLayout

        val imageView = ImageView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            200,
            200,
        )
        layoutParams.setMargins(0, 0, 16, 0)
        imageView.layoutParams = layoutParams
        imageView.setImageURI(uri)

        var isAlreadyAdded = false
        for (i in 0 until imageContainer.childCount) {
            val childView = imageContainer.getChildAt(i)
            if (childView is ImageView && childView.tag == uri.toString()) {
                isAlreadyAdded = true
                break
            }
        }

        if (!isAlreadyAdded) {
            imageView.tag = uri.toString() // Lưu trữ Uri của ảnh trong tag của ImageView để kiểm tra sau này
            imageView.setOnClickListener {
                imageContainer.removeView(imageView)
            }
            imageContainer.addView(imageView)
        }
    }

    private fun createQuestionDetailDialog(question: Question): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_question_detail, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)

        val backQuestionDetailBtn = sheet.findViewById<TextView>(R.id.backQuestionDetailBtn)
        val questionDetailTitle = sheet.findViewById<TextView>(R.id.questionDetailTitle)
        val questionDetailAskerName = sheet.findViewById<TextView>(R.id.questionDetailAskerName)
        val questionDetailAskDate = sheet.findViewById<TextView>(R.id.questionDetailAskDate)
        val questionDetailLectureId = sheet.findViewById<TextView>(R.id.questionDetailLectureId)
        val questionDetailContentView = sheet.findViewById<ConstraintLayout>(R.id.questionDetailContentView)
        val questionDetailContent = sheet.findViewById<TextView>(R.id.questionDetailContent)
        val questionDetailImage = sheet.findViewById<ImageView>(R.id.questionDetailImage)
        val replyListView = sheet.findViewById<RecyclerView>(R.id.replyListView)
        val cameraBtn1 = sheet.findViewById<Button>(R.id.cameraBtn1)
        val sendBtn = sheet.findViewById<Button>(R.id.sendBtn)
        val deleteQuestionBtn = sheet.findViewById<Button>(R.id.deleteQuestionBtn)
        val editQuestionBtn = sheet.findViewById<TextView>(R.id.editQuestionBtn)

        val dumpReply1 = Reply("John Doe", "Brad Schiff", "123", arrayListOf(), "I think you should do this I think you should do this I think you should do this", "14/03/2024")
        val dumpReply2 = Reply("John Doe", "Brad Schiff", "123", arrayListOf(), "I think you should do this", "14/03/2024")
        val dumpReplyList = listOf(dumpReply1, dumpReply2)
        //TODO: Get reply list by questionId from database
        //TODO: check if the question is asked by the user, if not, change the headpage to "New Question'

        questionDetailTitle.text = question.title
        questionDetailAskerName.text = question.asker
        val date: Date = originalFormat.parse(question.createdTime) ?: Date()
        val formattedDate: String = newFormat.format(date)
        questionDetailAskDate.text = formattedDate

        questionDetailLectureId.text = question.lectureId
        questionDetailContent.text = question.details
        if(question.images.isEmpty()){
            questionDetailImage.visibility = View.GONE
        }
        else {
            questionDetailImage.visibility = View.VISIBLE
        }


        val replyListAdapter = ReplyListAdapter(dumpReplyList)
        replyListView.adapter = replyListAdapter
        replyListView.layoutManager = LinearLayoutManager(requireContext())

        backQuestionDetailBtn.setOnClickListener{
            questionDetailDialog.dismiss()
        }

        cameraBtn1.setOnClickListener {
            startForImagePickerResult.launch(PickVisualMediaRequest())
        }

        sendBtn.setOnClickListener{
            //TODO: Submit reply to database
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