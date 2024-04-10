package com.deadlineshooters.yudemy.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
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
import com.deadlineshooters.yudemy.models.Image
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
import kotlin.properties.Delegates

class QADialog(private val courseId: String) : DialogFragment() {
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
    private lateinit var questionViewModel: QuestionViewModel
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())
    private var state by Delegates.notNull<Int>()
    private var imageList: ArrayList<String> = ArrayList()

    private lateinit var questionListAdapter: QuestionListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        questionViewModel = QuestionViewModel()
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

        questionViewModel.getQuestionsByCourseId(courseId)

        questionViewModel.questions.observe(this, Observer{ result ->
            questionListAdapter = QuestionListAdapter(result, this)
                questionListView.adapter = questionListAdapter
                questionListView.layoutManager = LinearLayoutManager(requireContext())

                questionListAdapter.onItemClick = { question ->
                    // TODO: check if the clicked question has asker = user._id
                    questionDetailDialog = createQuestionDetailDialog(question)
                    state = 2
                    questionDetailDialog.show()
                }
        })

        qaCloseBtn.setOnClickListener {
            dismiss()
        }

        startForImagePickerResult = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                uris.forEach { uri ->
                    addImageView(uri)
                }
            } else {
            }
        }


        addQuestionBtn.setOnClickListener {
            askQuestionDialog = createAskQuestionDialog()
            state = 1
            askQuestionDialog.show()
        }



        qaFilterBtn.setOnClickListener {
            filterQuestionDialog = createFilterQuestionDialog()
            filterQuestionDialog.show()
        }

    }

    private fun createAskQuestionDialog(): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_ask_question, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        val lectureViewModel: LectureViewModel = LectureViewModel()
        var lectureList = ArrayList<String>()
        val cancelAskBtn = sheet.findViewById<TextView>(R.id.cancelAskBtn)
        val lectureSpinner = sheet.findViewById<Spinner>(R.id.lectureSpinner)
        val cameraBtn = sheet.findViewById<Button>(R.id.editCameraBtn)
        val submitQuestionBtn = sheet.findViewById<TextView>(R.id.submitQuestionBtn)
        var selectedLecture: String = ""

        lectureViewModel.getLectureListByCourseId(courseId)
        lectureViewModel.lectures.observe(this, Observer { it ->
            for(lecture in it){
                lectureList.add(lecture.name)
            }
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lectureList)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                lectureSpinner.adapter = adapter
            }

            lectureSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedLecture = it[position]._id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        })

        cancelAskBtn.setOnClickListener{
            askQuestionDialog.dismiss()
        }

        cameraBtn.setOnClickListener {
            startForImagePickerResult.launch(PickVisualMediaRequest())
        }

        submitQuestionBtn.setOnClickListener{
            //TODO: Submit question to database
            val title = sheet.findViewById<TextView>(R.id.askQuestionTitle).text.toString()
            val details = sheet.findViewById<TextView>(R.id.askQuestionDetail).text.toString()

            CloudinaryHelper().uploadImageListToCloudinary (imageList){
                questionViewModel.addNewQuestion(courseId, BaseActivity().getCurrentUserID(), title, details, it, selectedLecture, originalFormat.format(Date()))
                askQuestionDialog.dismiss()
            }
        }

        dialog.setContentView(sheet)
        return dialog
    }

    private fun addImageView(uri: Uri) {
        val imageContainer = when (state) {
            1 -> askQuestionDialog.findViewById(R.id.questionImageContainer)
            2 -> questionDetailDialog.findViewById(R.id.repImageContainer)
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
        imageView.drawable?.let{drawble ->
            val bitmap = (drawble as BitmapDrawable).bitmap
            val filepath = BaseActivity().saveBitmapToFile(bitmap, requireContext())
            imageList.add(filepath.toString())
        }

        var isAlreadyAdded = false
        for (i in 0 until imageContainer.childCount) {
            val childView = imageContainer.getChildAt(i)

            if (childView is ImageView && childView.tag == uri.toString()) {
                isAlreadyAdded = true
                break
            }
        }

        if (!isAlreadyAdded) {
            imageView.tag = uri.toString()
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

        val replyListView = sheet.findViewById<RecyclerView>(R.id.replyListView)
        val cameraBtn1 = sheet.findViewById<Button>(R.id.cameraBtn1)
        val sendBtn = sheet.findViewById<Button>(R.id.sendBtn)
        val deleteQuestionBtn = sheet.findViewById<Button>(R.id.deleteQuestionBtn)
        val editQuestionBtn = sheet.findViewById<TextView>(R.id.editQuestionBtn)
        val questionDetailImageContainer = sheet.findViewById<LinearLayout>(R.id.questionDetailImageContainer)
        val repImageContainer = sheet.findViewById<LinearLayout>(R.id.repImageContainer)
        val replyContent = sheet.findViewById<TextView>(R.id.replyInput)

        lateinit var replyListAdapter: ReplyListAdapter
        val userViewModel: UserViewModel = UserViewModel()
        val lectureViewModel: LectureViewModel = LectureViewModel()
        val replyViewModel: ReplyViewModel = ReplyViewModel()

        //TODO: check if the question is asked by the user, if not, change the headpage to "New Question'

        userViewModel.getUserById(question.asker)
        lectureViewModel.getLectureById(question.lectureId)
        replyViewModel.getRepliesByQuestionId(question._id)

        questionDetailTitle.text = question.title
        userViewModel.userData.observe(this, Observer { it ->
            questionDetailAskerName.text = it.fullName
        })
        val date: Date = originalFormat.parse(question.createdTime) ?: Date()
        val formattedDate: String = newFormat.format(date)
        questionDetailAskDate.text = formattedDate

        lectureViewModel.lecture.observe(this, Observer { it ->
            questionDetailLectureId.text = it.name
        })
        questionDetailContent.text = question.details

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

        replyViewModel.replies.observe(this, Observer { it ->
            replyListAdapter = ReplyListAdapter(it, this)
            replyListView.adapter = replyListAdapter
            replyListView.layoutManager = LinearLayoutManager(requireContext())
        })

        backQuestionDetailBtn.setOnClickListener{
            questionDetailDialog.dismiss()
        }

        cameraBtn1.setOnClickListener {
            startForImagePickerResult.launch(PickVisualMediaRequest())
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
                }
            } else{
                val rep = Reply("", BaseActivity().getCurrentUserID(), question._id , ArrayList(), reply , originalFormat.format(Date()))
                replyViewModel.addNewReply(rep)
                replyContent.text = ""
                replyListAdapter.notifyItemInserted(replyListAdapter.itemCount)
            }

        }

        if(BaseActivity().getCurrentUserID() != question.asker){
            deleteQuestionBtn.visibility = View.GONE
            editQuestionBtn.visibility = View.GONE
        }

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
                    questionViewModel.deleteQuestion(courseId, question._id)
                    questionDetailDialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        editQuestionBtn.setOnClickListener{
            editQuestionDialog = createEditQuestionDialog(question)
            state = 3
            editQuestionDialog.show()
            editQuestionDialog.setOnDismissListener{
                questionViewModel.getQuestionsByCourseId(courseId)
                questionViewModel.getQuestionById(question._id)
                questionViewModel.question.observe(this, Observer { it ->
                    questionDetailDialog = createQuestionDetailDialog(it)
                    questionDetailDialog.show()
                })
            }
        }



        dialog.setContentView(sheet)
        return dialog
    }

    private fun createEditQuestionDialog(question: Question): Dialog {
        val sheet = layoutInflater.inflate(R.layout.dialog_edit_question, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        var lectureList = ArrayList<String>()
        val lectureViewModel: LectureViewModel = LectureViewModel()
        val cancelEditBtn = sheet.findViewById<TextView>(R.id.cancelEditBtn)
        val editLectureSpinner = sheet.findViewById<Spinner>(R.id.editLectureSpinner)
        val editCameraBtn = sheet.findViewById<Button>(R.id.editCameraBtn)
        val submitEditQuestionBtn = sheet.findViewById<TextView>(R.id.submitEditQuestionBtn)
        val title = sheet.findViewById<TextView>(R.id.editQuestionTitle)
        val details = sheet.findViewById<TextView>(R.id.editQuestionDetail)
        var selectedLecture: String = ""

        lectureViewModel.getLectureListByCourseId(courseId)
        lectureViewModel.lectures.observe(this, Observer { it ->
            for(lecture in it){
                lectureList.add(lecture.name)
            }
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lectureList)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    editLectureSpinner.adapter = adapter
                }

            editLectureSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedLecture = it[position]._id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        })
        title.text = question.title
        details.text = question.details

        var imgList = question.images
        for (imageUrl in question.images) {
            Log.d("QuestionListAdapter", question.images.toString())
            val imageView = ImageView(sheet.findViewById<HorizontalScrollView>(R.id.editQuestionImageView).context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                200,
                200
            )

            imageView.adjustViewBounds = true
            imageView.setPadding(0, 0, 16, 16)
            ImageViewHelper().setImageViewFromUrl(imageUrl, imageView)
            sheet.findViewById<LinearLayout>(R.id.editQuestionImageContainer).addView(imageView)
            imageView.setOnClickListener {
                sheet.findViewById<LinearLayout>(R.id.editQuestionImageContainer).removeView(imageView)
                imgList.removeAt(imgList.indexOf(imageUrl))
            }
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
            val questionTitle = title.text.toString()
            val questionDetails = details.text.toString()

            if(imageList.size == 0 && imgList.size == 0){
                questionViewModel.editQuestion(question._id,BaseActivity().getCurrentUserID(), questionTitle, questionDetails, ArrayList(), selectedLecture, question.createdTime)
                imageList.clear()
                questionListAdapter.notifyDataSetChanged()
                editQuestionDialog.dismiss()
            } else{
                CloudinaryHelper().uploadImageListToCloudinary(imageList){
                    val editedImageList = ArrayList<Image>().apply {
                        addAll(it)
                        addAll(imgList)
                    }
                    questionViewModel.editQuestion(question._id,BaseActivity().getCurrentUserID(), questionTitle, questionDetails, editedImageList, selectedLecture, question.createdTime)
                    imageList.clear()
                    questionListAdapter.notifyDataSetChanged()
                    editQuestionDialog.dismiss()
                }
            }
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
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        var adapter: BottomSheetDialogAdapter? = null
        when (state) {
            1 -> {
                adapter = BottomSheetDialogAdapter(resources.getStringArray(R.array.lectures_filter).toCollection(ArrayList()))
            }
            2 -> {
                adapter = BottomSheetDialogAdapter(resources.getStringArray(R.array.sort_most_recent_filter).toCollection(ArrayList()))
            }
            3 -> {
                adapter = BottomSheetDialogAdapter(resources.getStringArray(R.array.questions_filter).toCollection(ArrayList()))
            }
        }
        val rvFilters = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)

        rvFilters!!.adapter = adapter
        rvFilters.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters.addItemDecoration(itemDecoration)
        adapter?.onItemClick = { filter, filterIdx ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
            view.text = filter
            dialog.dismiss()
        }

//        dialog.setOnShowListener {
//            (bottomSheet.parent.parent as ViewGroup).background = ResourcesCompat.getDrawable(resources, R.color.dialog_background, null)
//        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }
}