package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.QuestionListAdapter
import com.deadlineshooters.yudemy.adapters.ReplyListAdapter
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QAFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QAFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var qaCloseBtn: TextView
    private lateinit var qaFilterBtn: Button
    private lateinit var addQuestionBtn: Button
    private lateinit var questionListView: RecyclerView
    private lateinit var askQuestionDialog: Dialog
    private lateinit var questionDetailDialog: Dialog
    private lateinit var startForImagePickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())
    private var state by Delegates.notNull<Int>()


    private val dumpQuestion1 = Question("123", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion2 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestion3 = Question("124", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
    private val dumpQuestionList = arrayListOf(dumpQuestion1, dumpQuestion2, dumpQuestion3)

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
        return inflater.inflate(R.layout.fragment_q_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qaCloseBtn = view.findViewById(R.id.qaCloseBtn)
        qaFilterBtn = view.findViewById(R.id.qaFilterBtn)
        addQuestionBtn = view.findViewById(R.id.addQuestionBtn)
        questionListView = view.findViewById(R.id.questionListView)

        questionListAdapter = QuestionListAdapter(dumpQuestionList)
        questionListView.adapter = questionListAdapter
        questionListView.layoutManager = LinearLayoutManager(requireContext())


        qaCloseBtn.setOnClickListener {
            //TODO: Close the activity
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


        addQuestionBtn.setOnClickListener {
            askQuestionDialog = createAskQuestionDialog()
            state = 1
            askQuestionDialog.show()
        }

        questionListAdapter.onItemClick = { question ->
            questionDetailDialog = createQuestionDetailDialog(question)
            state = 2
            questionDetailDialog.show()
        }

    }

    private fun createAskQuestionDialog(): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_ask_question, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        Log.d("Sheet", sheet.toString())
        val dumpLectureList = listOf("Lecture 1", "Lecture 2", "Lecture 3")
        val cancelAskBtn = sheet.findViewById<TextView>(R.id.cancelAskBtn)
        val lectureSpinner = sheet.findViewById<Spinner>(R.id.lectureSpinner)
        val cameraBtn = sheet.findViewById<Button>(R.id.cameraBtn)
        val submitQuestionBtn = sheet.findViewById<TextView>(R.id.submitQuestionBtn)


        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dumpLectureList)
            .also { adapter ->
            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
            lectureSpinner.adapter = adapter
        }



        cancelAskBtn.setOnClickListener{
            askQuestionDialog.dismiss()
        }

        cameraBtn.setOnClickListener {
            startForImagePickerResult.launch(PickVisualMediaRequest())
        }

        submitQuestionBtn.setOnClickListener{
            //TODO: Submit question to database
        }

        dialog.setContentView(sheet)
        return dialog
    }

    private fun addImageView(uri: Uri) {
        var imageContainer: LinearLayout = LinearLayout(requireContext())
        if(state == 1){
            imageContainer = askQuestionDialog.findViewById(R.id.questionImageContainer)
        }
        if(state == 2){
            imageContainer = questionDetailDialog.findViewById(R.id.replyImageContainer)
        }

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


    fun generateDummyData(): ArrayList<Question> {
        //TODO: get questions from database
        val questionList = ArrayList<Question>()
        for(i in 1..20) {
            val dumpQuestion = Question("123", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
            questionList.add(dumpQuestion)
        }
        return questionList
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

        val dumpReply1 = Reply("John Doe", "Brad Schiff", "123", arrayListOf(), "I think you should do this I think you should do this I think you should do this", "14/03/2024")
        val dumpReply2 = Reply("John Doe", "Brad Schiff", "123", arrayListOf(), "I think you should do this", "14/03/2024")
        val dumpReplyList = listOf(dumpReply1, dumpReply2)
        //TODO: Get reply list by questionId from database

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
         * @return A new instance of fragment QAFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QAFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}