package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.contains
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.QuestionListAdapter
import com.deadlineshooters.yudemy.models.Question

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
    private lateinit var startForImagePickerResult: ActivityResultLauncher<PickVisualMediaRequest>



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
            askQuestionDialog.show()
        }

    }

    private fun createAskQuestionDialog(): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        val sheet = layoutInflater.inflate(R.layout.dialog_ask_question, null)
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
        val imageContainer = askQuestionDialog.findViewById<LinearLayout>(R.id.imageContainer)
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