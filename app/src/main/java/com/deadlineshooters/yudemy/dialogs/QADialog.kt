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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class QADialog : DialogFragment() {
    private lateinit var qaCloseBtn: TextView
    private lateinit var qaFilterBtn: Button
    private lateinit var addQuestionBtn: Button
    private lateinit var questionListView: RecyclerView
    private lateinit var askQuestionDialog: Dialog
    private lateinit var questionDetailDialog: Dialog
    private lateinit var editQuestionDialog: Dialog
    private lateinit var filterQuestionDialog: Dialog
    private lateinit var filterByLecturesDialog: Dialog
    private lateinit var filterSortMostRecentDialog: Dialog
    private lateinit var filterByQuestionDialog: Dialog
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
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
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
            //TODO: Close the fragment
            dismiss()
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
            // TODO: check if the clicked question has asker = user._id
            questionDetailDialog = createQuestionDetailDialog(question)
            state = 2
            questionDetailDialog.show()
        }

        qaFilterBtn.setOnClickListener {
            filterQuestionDialog = createFilterQuestionDialog()
            filterQuestionDialog.show()
        }

    }

    private fun createAskQuestionDialog(): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_ask_question, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        val dumpLectureList = listOf("Lecture 1", "Lecture 2", "Lecture 3")
        val cancelAskBtn = sheet.findViewById<TextView>(R.id.cancelAskBtn)
        val lectureSpinner = sheet.findViewById<Spinner>(R.id.lectureSpinner)
        val cameraBtn = sheet.findViewById<Button>(R.id.editCameraBtn)
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
        val imageContainer = when (state) {
            1 -> askQuestionDialog.findViewById(R.id.questionImageContainer)
            2 -> questionDetailDialog.findViewById(R.id.replyImageContainer)
            3 -> editQuestionDialog.findViewById(R.id.editQuestionImageContainer)
            else -> LinearLayout(requireContext())
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
//        for(i in 1..20) {
//            val dumpQuestion = Question("123", "John Doe", "456", "How to do this?", "I'm having trouble with this, can someone help me?", arrayListOf(), "13/03/2024")
//            questionList.add(dumpQuestion)
//        }
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

        //TODO: check if the question is asked by the user, if not, hide the buttons (uncomment the 2 lines below), and change the headpage to "New Question'
//        deleteQuestionBtn.visibility = View.GONE
//        editQuestionBtn.visibility = View.GONE

        deleteQuestionBtn.setOnClickListener{
            //TODO: Delete question from database
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage("Are you sure you want delete this question? This action cannot be undone.")
                .setTitle("Delete question")
                .setNegativeButton(Html.fromHtml("<font color='#00000FF'><b>Cancel</b></font>")) { dialog, which ->

                }
                .setPositiveButton(Html.fromHtml("<font color='#FF0000'><b>Delete</b></font>")) { dialog, which ->
                    //TODO: Delete question from database
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        editQuestionBtn.setOnClickListener{
            editQuestionDialog = createEditQuestionDialog()
//            askQuestionDialog = createAskQuestionDialog()

            state = 3
            editQuestionDialog.show()
        }

        dialog.setContentView(sheet)
        return dialog
    }

    private fun createEditQuestionDialog(): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_edit_question, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        val dumpLectureList = listOf("Lecture 1", "Lecture 2", "Lecture 3")
        val cancelEditBtn = sheet.findViewById<TextView>(R.id.cancelEditBtn)
        val editLectureSpinner = sheet.findViewById<Spinner>(R.id.editLectureSpinner)
        val editCameraBtn = sheet.findViewById<Button>(R.id.editCameraBtn)
        val submitEditQuestionBtn = sheet.findViewById<TextView>(R.id.submitEditQuestionBtn)


        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dumpLectureList)
            .also { adapter ->
                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item)
                editLectureSpinner.adapter = adapter
            }

        cancelEditBtn.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage("Are you sure you want to discard the changes?")
                .setTitle("Please Confirm")
                .setNegativeButton(Html.fromHtml("<font color='#00000FF'><b>Cancel</b></font>")) { dialog, which ->

                }
                .setPositiveButton(Html.fromHtml("<font color='#FF0000'><b>Discard</b></font>")) { dialog, which ->
                    editQuestionDialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        editCameraBtn.setOnClickListener {
            startForImagePickerResult.launch(PickVisualMediaRequest())
        }

        submitEditQuestionBtn.setOnClickListener{
            //TODO: Submit question to database
        }

        dialog.setContentView(sheet)
        return dialog
    }

    private fun createFilterQuestionDialog(): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_filter_question, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        val closeFilterBtn = sheet.findViewById<TextView>(R.id.closeFilterBtn)
        val resetFilterBtn = sheet.findViewById<TextView>(R.id.resetFilterBtn)
        val lectureFilter = sheet.findViewById<TextView>(R.id.lectureFilter)
        val sortMostRecentFilter = sheet.findViewById<TextView>(R.id.sortMostRecentFilter)
        val allQuestionsFilter= sheet.findViewById<TextView>(R.id.allQuestionsFilter)
        val applyFilerBtn = sheet.findViewById<Button>(R.id.applyFilerBtn)

        filterByLecturesDialog = createQuestionFilterDialog(lectureFilter, 1)
        filterSortMostRecentDialog = createQuestionFilterDialog(sortMostRecentFilter, 2)
        filterByQuestionDialog = createQuestionFilterDialog(allQuestionsFilter, 3)

        lectureFilter.setOnClickListener {
            filterByLecturesDialog.show()
        }
        sortMostRecentFilter.setOnClickListener {
            filterSortMostRecentDialog.show()
        }
        allQuestionsFilter.setOnClickListener {
            filterByQuestionDialog.show()
        }

        closeFilterBtn.setOnClickListener{
            filterQuestionDialog.dismiss()
        }

        resetFilterBtn.setOnClickListener{
            lectureFilter.text = "All lectures"
            sortMostRecentFilter.text = "Sort by most recent"
            allQuestionsFilter.text = "All questions"
        }

        applyFilerBtn.setOnClickListener{
            //TODO: Apply filter and find questions from database
        }

        dialog.setContentView(sheet)
        return dialog
    }

    private fun createQuestionFilterDialog(view: TextView, state: Int): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext(), android.R.style.Theme_Material_Light)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_my_learning_filter, null)
        var adapter: MyLearningFilterAdapter? = null
        when (state) {
            1 -> {
                adapter = MyLearningFilterAdapter(resources.getStringArray(R.array.lectures_filter))
            }
            2 -> {
                adapter = MyLearningFilterAdapter(resources.getStringArray(R.array.sort_most_recent_filter))
            }
            3 -> {
                adapter = MyLearningFilterAdapter(resources.getStringArray(R.array.questions_filter))
            }
        }
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter?.onItemClick = { filter ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
            view.text = filter
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            (bottomSheet.parent.parent as ViewGroup).background = ResourcesCompat.getDrawable(resources, R.color.dialog_background, null)
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }
}